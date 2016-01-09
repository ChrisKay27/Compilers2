package admininstration;

import parser.Parser;
import scanner.Scanner;

import java.io.*;
import java.nio.charset.Charset;

public class Administration implements Administrator {

    private Scanner scanner;
    private Parser parser;


    public Administration(Reader reader) throws IOException {

        this.scanner = new Scanner(reader);
        parser = new Parser(this.scanner);
        parser.startParsing();
    }


    public static void main(String[] args) throws FileNotFoundException {
        File input = new File("input.cs16");
        Charset encoding = Charset.forName("ascii");

        try (InputStream in = new FileInputStream(input);
             Reader reader = new InputStreamReader(in, encoding);
             Reader buffer = new BufferedReader(reader))
        {
            Administration admin = new Administration(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
