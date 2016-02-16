package admininstration;

import parser.Parser;
import parser.grammar.ASTNode;
import scanner.Scanner;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Supplier;

public class Administration implements Administrator {
    // development
    private static final boolean debug = true;

    private Supplier<Integer> fileInput;
    private java.util.Scanner fileScanner;

    public static boolean debug() {
        return debug;
    }
    // end development

    private BufferedWriter errorWriter, outputWriter;
    private final Options options;
    protected OutputHandler errorReporter = new OutputHandler(System.err::println);

    protected Scanner scanner;
    protected Parser parser;

    private String currentLine;

    public Administration(Options options) throws IOException, UnrecognizedSourceCodeException {

        this.options = options;

        this.initReader(options.inputFilePath);

        if( options.errorFilePath != null )
            initErrorFile(options.errorFilePath);
        if( options.outputFilePath != null )
            initOutputFileWriter(options.outputFilePath);

        this.scanner = new Scanner(fileInput,this::printLineTrace,this::printErrorMessage);
        scanner.setTraceEnabled(options.verbose);
        this.parser = new Parser(this.scanner,this::printLineTrace,this::printErrorMessage);
        parser.setTraceEnabled(options.verbose);

    }

    /**
     * starts the parser
     *
     * @throws IOException
     */
    public void compile() throws IOException {
        ASTNode tree = parser.startParsing();
        if( options.verbose )
            if(tree != null)
                System.out.println("Compile Successful");
            else
                System.out.println("Compile Failed");
        if( tree != null && options.printAST ){
            printLineTrace("---- Abstract Syntax Tree ----");
            printLineTrace(tree.toString());
        }
    }

    /**
     * closes the buffered reader
     *
     * @throws IOException
     */
    public void close() throws IOException {
        fileScanner.close();
        if( errorWriter != null ) {
            //System.out.println("Closing error writer");
            errorWriter.flush();
            errorWriter.close();
        }
        if( outputWriter != null ) {
            //System.out.println("Closing output writer");
            outputWriter.flush();
            outputWriter.close();
        }
    }

    public void printLineTrace(String line){
        if( outputWriter != null ){
            try {
                outputWriter.write(line + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            System.out.println(line);
    }

    public void printErrorMessage(String msg){
        errorReporter.print(msg);
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

            File input = new File(path);
            Charset encoding = Charset.forName("ascii");

            try {
                InputStream in = new FileInputStream(input);
                Reader reader = new InputStreamReader(in, encoding);
                fileScanner = new java.util.Scanner(reader);

                currentLine = fileScanner.nextLine();
                fileInput = () -> {
                    if (currentLine.length() == 0 ) {
                        if(fileScanner.hasNextLine()) {
                            currentLine = fileScanner.nextLine();
                            int newline = '\n';
                            return newline;
                        }else
                            return -1;
                    }
                    int c = currentLine.charAt(0);
                    currentLine = currentLine.substring(1,currentLine.length());
                    return c;
                };

//                new java.util.Scanner(reader).nextLine()
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else throw new UnrecognizedSourceCodeException("The file located at" + path + "is not a .cs16 file");
    }

    private void initErrorFile(String errorFilePath) {
        try {
            File input = new File(errorFilePath);
            if(!input.exists()) {
                boolean success = input.createNewFile();
                if( !success )
                    throw new RuntimeException("Cannot create error output file: " + errorFilePath);
            }

            OutputStream in = new FileOutputStream(input);
            Writer writer = new PrintWriter(in);
            errorWriter = new BufferedWriter(writer);
            errorReporter.setOutput((str) -> {
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
            if(!input.exists()) {
                boolean success = input.createNewFile();
                if( !success )
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

}
