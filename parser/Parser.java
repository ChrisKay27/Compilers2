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

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public void startParsing() throws IOException {
        List<Token> tokens = new ArrayList<>();

        Token nextToken = scanner.nextToken();

        while(nextToken.token != Tokens.ENDFILE){
            tokens.add(nextToken);
            nextToken = scanner.nextToken();
        }

        System.out.println(tokens);
    }
}
