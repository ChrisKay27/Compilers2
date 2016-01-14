package admininstration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parser.Parser;
import scanner.Scanner;

import java.io.*;
import java.nio.charset.Charset;

public class Administration implements Administrator {
    // development
    private static final boolean debug = false;

    public static boolean debug() {
        return debug;
    }
    // end development
    protected ErrorReporter errorReporter = new ErrorReporter(System.out::println);
    protected Reader buffer;
    protected Scanner scanner;
    protected Parser parser;

    public Administration(@NotNull String path, @Nullable String errorFilePath) throws IOException, UnrecognizedSourceCodeException {
        this.initReader(path);

        if( errorFilePath != null )
            initErrorFile(errorFilePath);


        this.scanner = new Scanner(buffer,this::printLineTrace,this::printErrorMessage);
        this.parser = new Parser(this.scanner,this::printErrorMessage);
    }



    /**
     * starts the parser
     *
     * @throws IOException
     */
    public void compile() throws IOException {
        this.parser.startParsing();
    }

    /**
     * closes the buffered reader
     *
     * @throws IOException
     */
    public void close() throws IOException {
        buffer.close();
    }

    public void printLineTrace(String line){
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
                this.buffer = new BufferedReader(reader);
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
                    throw new RuntimeException("Cannot create output file: " + errorFilePath);
            }

            OutputStream in = new FileOutputStream(input);
            Writer writer = new PrintWriter(in);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            errorReporter.setOutput((str) -> {
                try {
                    bufferedWriter.write(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isCs16File(String path) {
        return path.substring(path.length() - 5, path.length()).equals(".cs16");
    }

}
