package parser;

import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Chris on 1/9/2016.
 */
public class Parser {

    private static String[] libraries = {
            "int readint(void);",
            "void writeint(int x);",
            "bool readbool(void);",
            "void writebool(bool x);"
    };

    private final Scanner scanner;
    private final Consumer<String> errorOutput;
    private List<Token> tokens;

    public Parser(Scanner scanner, Consumer<String> errorOutput) {
        this.scanner = scanner;
        this.errorOutput = errorOutput;
        tokens = new ArrayList<>();
    }

    public List<Token> getTokenList() {
        return this.tokens;
    }

    /**
     * @return String which represents the abstract syntax tree,
     * at this stage it returns the string of tokens received from the scanner
     * @throws IOException
     */
    public boolean startParsing() throws IOException {
        injectLibraries();

        boolean pass = parse();

        return pass;
    }

    public void injectLibraries() throws java.io.IOException {

        boolean temp = scanner.isTraceEnabled();
        scanner.setTraceEnabled(false);

        for (String foo : libraries) {

            parseLine(foo);

        }

        scanner.setTraceEnabled(temp);
    }

    public boolean parse() throws java.io.IOException {

        Token nextToken;
        boolean pass = true;
        do {
            nextToken = scanner.nextToken();
            if (nextToken.token == Tokens.ERROR)
                pass = false;

            tokens.add(nextToken);
        } while (nextToken.token != Tokens.ENDFILE);

        System.out.println("Tokens: " + tokens);
        return pass;
    }

    public void parseLine(String line) throws java.io.IOException {
        Reader fileReader = this.scanner.redirectReader(new StringReader(line));

        parse();

        this.scanner.redirectReader(fileReader);
    }
}
