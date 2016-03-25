package admininstration;

import parser.Parser;
import parser.TokenType;
import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import scanner.Scanner;
import scanner.Token;
import semanticAnalyzer.SemanticAnalyzer;
import tupleGeneration.QuadrupleGenerator;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.System.err;
import static java.lang.System.out;

public class Administration implements Administrator {
    // development
    private static final boolean debug = false;


    public static boolean debug() {
        return debug;
    }
    // end development


    private BufferedWriter errorWriter, outputWriter;
    private final Options options;
    protected OutputHandler outputHandler;

    private Supplier<Integer> fileInput;
    private java.util.Scanner fileScanner;
    private List<String> fileLines = new ArrayList<>();
    private String currentLine;
    private String currentLineFeed;
    private String lastLine;
    //Keeping track of the current line number
    private int lineNumber = 0;

    protected Scanner scanner;
    protected Parser parser;


    /**
     * @param options Used to specify which phase to parse to, the volume level of the compile (verbose or quiet), and
     *                the input and output file.
     */
    public Administration(Options options) throws IOException, UnrecognizedSourceCodeException {

        this.options = options;
        this.outputHandler = new OutputHandler(err::println);
        //There is no code at line 0
        fileLines.add("");

        //initializes the error and output file writers
        if (options.outputFilePath != null)
            initOutputFileWriter(options.outputFilePath);

        if (options.errorFilePath != null)
            initErrorFile(options.errorFilePath);
        else {
            outputHandler.setErrorOutput(System.out::println);
        }

        //Prints some information to the user about to what phase of compiling is being performed.
        if (options.quiet)
            System.out.println("Compiling up to the " + options.getPhase());
        printLineTrace("Compiling up to the " + options.getPhase() + '\n');

        //inits the input file reader
        this.initReader(options.inputFilePath);

        this.scanner = new Scanner(fileInput, this::printTokensOnCurrentLine, this::printLineTrace, this::printErrorMessage);
        scanner.setTraceEnabled(options.verbose);

        this.parser = new Parser(this.scanner, this::printParserLineTrace, this::printErrorMessage, this::getCurrentLine);
        parser.setTraceEnabled(options.verbose);

    }

    /**
     * starts the parser
     *
     * @throws IOException
     */
    public void compile() throws IOException {

        if (options.fullCompile) {
            //fullCompile();
        } else if (options.tuplePhase) {
            tupleCompile();
        } else if (options.semanticPhase) {
            semanticCompile();
        } else if (options.parserPhase) {
            parserCompile();
        } else if (options.lexicalPhase) {
            lexicalCompile();
        }
    }

    private void lexicalCompile() throws IOException {
        List<Token> tokens = new ArrayList<>();
        Token t = null;
        do {
            t = scanner.nextToken();
            tokens.add(t);
        } while (t.token != TokenType.ENDFILE);
        if (this.outputWriter != null) {
            StringBuilder sb = new StringBuilder();
            for (Token token : tokens)
                sb.append(t.toString());
            this.outputWriter.write(sb.toString());
        }
    }

    private void parserCompile() throws IOException {
        ASTNode tree = parser.startParsing();

        printASTTree(tree);
        printCompilationResults(tree);
        if (this.outputWriter != null) {
            this.outputWriter.write(tree.toString());
        }
    }

    private void semanticCompile() throws IOException {

        ASTNode tree = parser.startParsing();

        if (tree == null) {
            out.println("\n-------------------------------------\n");
            out.println("\tCompile Failed at Parser phase\n");

            return;
        }

        printASTTree(tree);

        printLineTrace("\n  ----  Parsing Phase Complete  ----\n\n");
        printLineTrace("  ----  Starting Semantics Phase  ----\n\n");
        SemanticAnalyzer semAnal = new SemanticAnalyzer(tree, this::printLineTrace, this::printErrorMessage, this::printErrorMessage);

        boolean passed = semAnal.startSemAnal((Declaration) tree);
        if (passed || options.unitTesting) {
            printASTTree(tree);
            printCompilationResults(tree);
        } else {
            //If no tree was returned then the compiling failed
            out.println("\n-------------------------------------\n");
            out.println("\tCompile Failed\n");

            //And we print the error messages to the user
            if (!options.unitTesting)
                outputHandler.printErrors(out::println);
        }

        if (this.outputWriter != null) {
            outputWriter.write(tree.toString());
        }
    }

    private void tupleCompile() throws IOException {

        ASTNode tree = parser.startParsing();

        if (tree == null) {
            out.println("\n-------------------------------------\n");
            out.println("\tCompile Failed at Parser phase\n");

            return;
        }

        printASTTree(tree);

        printLineTrace("\n  ----  Parsing Phase Complete  ----\n\n");
        printLineTrace("  ----  Starting Semantics Phase  ----\n\n");
        SemanticAnalyzer semAnal = new SemanticAnalyzer(tree, this::printLineTrace, this::printErrorMessage, this::printErrorMessage);

        boolean passed = semAnal.startSemAnal((Declaration) tree);
        if (passed || options.unitTesting) {
            printASTTree(tree);
            printCompilationResults(tree);


            String code = new QuadrupleGenerator(tree, this::printLineTrace, this::printErrorMessage, this::printErrorMessage).startCodeGeneration((Declaration) tree);

            out.println("\n  Code Generation Complete");
            out.println("\n------------------------------\n");
            out.println(code);

            //this.outputHandler.printOutputs(out::println);
        } else {
            //If no tree was returned then the compiling failed
            out.println("\n-------------------------------------\n");
            out.println("\tCompile Failed\n");

            //And we print the error messages to the user
            if (!options.unitTesting)
                outputHandler.printErrors(out::println);
        }
        if (this.outputWriter != null) {
            this.outputWriter.write(tree.getCode());
        }
    }

    public void printASTTree(ASTNode tree) {
        //if a tree was returned we decide to print it or not based on a program argument
        if (tree != null && options.printAST) {
            printLineTrace("\n\n---- Abstract Syntax Tree ----\n\n");
            printLineTrace(tree.toString());
        }
    }

    public void printCompilationResults(ASTNode tree) {

        if (tree != null) {
            //If it returned a tree we report success
            if (!options.unitTesting)
                out.println("\n\tCompile Successful");
        } else {
            //If no tree was returned then the compiling failed
            out.println("\n-------------------------------------\n");
            out.println("\tCompile Failed\n");

            //And we print the error messages to the user
            if (!options.unitTesting)
                outputHandler.printErrors(out::println);
        }
    }

    /**
     * Used to collect the line + all the tokens on that line.
     * Not currently being used though
     */
    public void printTokensOnCurrentLine(List<Token> tokens) {
        if (options.unitTesting) return;

        if (!tokens.isEmpty() && options.verbose) {
            StringBuilder sb = new StringBuilder();
            tokens.forEach(t -> sb.append('\n').append(lineNumber).append(":\t\t").append(t));

            outputHandler.addScannerOutput(lineNumber + ": " + currentLine.trim(), sb.toString() + "\n\n");
        }
    }

    /**
     * Prints an message out to standard out or to the output file
     */
    public void printLineTrace(String line) {
        if (options.unitTesting) return;

        if (options.verbose)
            if (outputWriter != null) {
                try {
                    outputWriter.write(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                out.print(line);
    }

    /**
     * Prints messages from the parser
     */
    public void printParserLineTrace(String trace) {
        outputHandler.addParseOutput(lineNumber + ": " + currentLine.trim(), lineNumber + ": " + trace);

        if (options.verbose) {
            printLineTrace(trace + '\n');
        }
    }

    /**
     * Prints error messages from the scanner or parser to the appropriate output
     */
    public void printErrorMessage(String msg) {
        String errorMsg = (lineNumber) + ": " + currentLine + (lineNumber) + ":" + msg;
        if (!options.unitTesting && !options.quiet)
            outputHandler.printErrorMessage(errorMsg);
        outputHandler.addErrorMessage(errorMsg);
    }

    /**
     * Prints error messages from the scanner or parser to the appropriate output
     */
    public void printErrorMessage(int lineNumber, String msg) {
        String errorMsg = (lineNumber) + ": " + fileLines.get(lineNumber) + (lineNumber) + ": " + msg;
        if (!options.unitTesting && !options.quiet)
            outputHandler.printErrorMessage(errorMsg);
        outputHandler.addErrorMessage(errorMsg);
    }

    /**
     * initializes the buffered reader to the source code file
     * if the file does not have the .cs16 file extension then this method throws an UnrecognizedSourceCodeException
     *
     * @param path - path to the .cs16 source file
     * @throws UnrecognizedSourceCodeException
     */
    private void initReader(String path) throws UnrecognizedSourceCodeException {
        if (isCs16File(path)) { // verifies that the source code file has the correct extension

            if (options.quiet)
                System.out.println("Reading " + path);
            printLineTrace("Reading " + path + '\n');

            File input = new File(path);
            Charset encoding = Charset.forName("ascii");

            try {
                InputStream in = new FileInputStream(input);
                Reader reader = new InputStreamReader(in, encoding);
                fileScanner = new java.util.Scanner(reader);
                String curLine = "";
                if (fileScanner.hasNext())
                    curLine = fileScanner.nextLine();
                currentLineFeed = curLine + '\n';
                currentLine = currentLineFeed;
                lineNumber = 1;

                //Keeps track of the lines in the file for error output
                fileLines.add(currentLine);

                if (options.verbose && !curLine.trim().isEmpty())
                    printLineTrace("\n" + lineNumber + ": " + curLine);

                fileInput = () -> {
                    if (currentLineFeed.length() == 0) {
                        if (fileScanner.hasNextLine()) {
                            String currLine = fileScanner.nextLine();
                            currentLineFeed = currLine + '\n';
                            currentLine = currentLineFeed;
                            lineNumber++;

                            //Keeps track of the lines in the file for error output
                            fileLines.add(currentLine);

                            if (options.verbose && !currLine.trim().isEmpty())
                                printLineTrace("\n" + lineNumber + ": " + currLine);


                        } else
                            return -1;
                    }
                    int c = currentLineFeed.charAt(0);
                    currentLineFeed = currentLineFeed.substring(1, currentLineFeed.length());
                    return c;
                };

//                new java.util.Scanner(reader).nextLine()
            } catch (Exception e) {
                outputHandler.printErrorMessage(e.getMessage());
//                System.out.println(e);
            }
        } else throw new UnrecognizedSourceCodeException("The file located at" + path + "is not a .cs16 file");
    }

    private void initErrorFile(String errorFilePath) {
        try {
            File input = new File(errorFilePath);
            if (!input.exists()) {
                boolean success = input.createNewFile();
                if (!success)
                    throw new RuntimeException("Cannot create error output file: " + errorFilePath);
            }

            OutputStream in = new FileOutputStream(input);
            Writer writer = new PrintWriter(in);
            errorWriter = new BufferedWriter(writer);
            outputHandler.setErrorOutput((str) -> {
                err.println("Error on line: " + lineNumber + " - " + str);
                try {
                    errorWriter.write(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initOutputFileWriter(String outputFilePath) {
        try {
            File input = new File(outputFilePath);
            if (!input.exists()) {
                boolean success = input.createNewFile();
                if (!success)
                    throw new RuntimeException("Cannot create output file: " + outputFilePath);
            }

            OutputStream in = new FileOutputStream(input);
            Writer writer = new PrintWriter(in);
            outputWriter = new BufferedWriter(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isCs16File(String path) {
        return path.substring(path.length() - 5, path.length()).equals(".cs16");
    }

    /**
     * Closes the buffered reader
     */
    public void close() throws IOException {
        if (fileScanner != null)
            fileScanner.close();

        if (errorWriter != null) {
            //System.out.println("Closing error writer");
            errorWriter.flush();
            errorWriter.close();
        }
        if (outputWriter != null) {
            //System.out.println("Closing output writer");
            outputWriter.flush();
            outputWriter.close();
        }
    }

    public OutputHandler getOutputHandler() {
        return outputHandler;
    }

    public int getCurrentLine() {
        return this.lineNumber;
    }
}
