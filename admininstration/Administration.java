package admininstration;

import parser.Parser;
import scanner.Scanner;

import java.io.*;
import java.nio.charset.Charset;

public class Administration implements Administrator {
    // development
    private static final boolean debug = true;

    public static boolean debug() {
        return debug;
    }
    // end development

    protected Reader buffer;
    protected Scanner scanner;
    protected Parser parser;

    public Administration(String path) throws IOException, UnrecognizedSourceCodeException {
        this.initReader(path);
        this.scanner = new Scanner(buffer);
        this.parser = new Parser(this.scanner);
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
            } finally {

            }
        } else throw new UnrecognizedSourceCodeException("The file located at" + path + "is not a .cs16 file");
    }

    public boolean isCs16File(String path) {
        return path.substring(path.length() - 5, path.length()).equals(".cs16");
    }

}
