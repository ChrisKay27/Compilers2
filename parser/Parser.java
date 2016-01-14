package parser;

import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
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
     * at this stage it retuns the string of tokens recieved from the scanner
     * @throws IOException
     */
    public String startParsing() throws IOException {

        Token nextToken;

        do{
            nextToken = scanner.nextToken();
            tokens.add(nextToken);
        }while(nextToken.token != Tokens.ENDFILE);

        return tokens.toString();
    }
}
