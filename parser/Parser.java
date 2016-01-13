package parser;

import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Chris on 1/9/2016.
 */
public class Parser {

    private final Scanner scanner;
    private ArrayList<Token> tokens;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        tokens = new ArrayList<>();
    }

    public ArrayList<Token> getTokenString() {
        return this.tokens;
    }

    /**
     * @return String which represents the abstract syntax tree,
     * at this stage it retuns the string of tokens recieved from the scanner
     * @throws IOException
     */
    public String startParsing() throws IOException {

        Token nextToken = scanner.nextToken();

        while(nextToken.token != Tokens.ENDFILE){
            tokens.add(nextToken);
            nextToken = scanner.nextToken();
        }

        return tokens.toString();
    }
}
