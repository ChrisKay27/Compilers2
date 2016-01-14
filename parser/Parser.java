package parser;

import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 1/9/2016.
 */
public class Parser {

    private final Scanner scanner;
    private List<Token> tokens;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        tokens = new ArrayList<>();
    }

    public List<Token> getTokenList() {
        return this.tokens;
    }

    /**
     * @return String which represents the abstract syntax tree,
     * at this stage it returns the string of tokens recieved from the scanner
     * @throws IOException
     */
    public String startParsing() throws IOException {

        // Inject library functions into parser before parsing this file
        injectLibraryFunctions();
        // parse this file
        parse();

        return tokens.toString();
    }

    /**
     * Inserts library functions into environment for every file parsed.
     * Must not report the parsing of these functions on stdout.
     * <p>
     * Library Functions are;
     * int readint(void);
     * void writeint(int x);
     * bool readbool(void);
     * void writebool(bool x);
     */
    private void injectLibraryFunctions() throws IOException {
        String[] library = {
                "int readint(void);",
                "void writeint(int x);",
                "bool readbool(void);",
                "void writebool(bool x);"
        };
        for (String foo : library) {
            Reader temp = this.scanner.setStringReader(foo);
            parse();
            Token eof = this.tokens.remove(tokens.size() - 1);
            this.scanner.setReader(temp);
        }
    }

    private void parse() throws IOException {
        Token nextToken;
        do {
            nextToken = scanner.nextToken();
            tokens.add(nextToken);
        } while (nextToken.token != Tokens.ENDFILE);
    }
}
