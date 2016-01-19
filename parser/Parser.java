package parser;

import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Chris on 1/9/2016.
 */
public class Parser {

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

        Token nextToken;
        boolean pass = true;
        do{
            nextToken = scanner.nextToken();
            if( nextToken.token == Tokens.ERROR )
                pass = false;
            tokens.add(nextToken);
        }while(nextToken.token != Tokens.ENDFILE);

        return pass;
    }
}
