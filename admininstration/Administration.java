package admininstration;

import parser.Parser;
import scanner.Scanner;

import java.io.*;
import java.nio.charset.Charset;

public class Administration implements Administrator {
    // development
    private static final boolean debug = true;
    public static boolean debug(){return debug;}
    // end development

    private Scanner scanner;
    private Parser parser;


    public Administration(Reader reader) throws IOException {

        this.scanner = new Scanner(reader);
        this.parser = new Parser(this.scanner);
        this.parser.startParsing();
    }


    public static void main(String[] args) throws FileNotFoundException {
        File testCases = new File("src/testCases/");

        for(File testCase : testCases.listFiles()){
            if( !testCase.getName().equals("commentHell.cs16")) continue;
            System.out.println("Attempting test: " + testCase.getName());
            Charset encoding = Charset.forName("ascii");

            try (InputStream in = new FileInputStream(testCase);
                 Reader reader = new InputStreamReader(in, encoding);
                 Reader buffer = new BufferedReader(reader))
            {
                Administration admin = new Administration(buffer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
