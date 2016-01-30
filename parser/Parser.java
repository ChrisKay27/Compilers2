package parser;

import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Chris on 1/9/2016.
 */
public class Parser {

    private Grammar g;

    // standard library
    private static String[] libraries = {
            "int readint(void);",
            "void writeint(int x);",
            "bool readbool(void);",
            "void writebool(bool x);"
    };

    private final Scanner scanner;
    private final Consumer<String> errorOutput;
    private List<Token> tokens;

    private Tokens lookahead;

    public Parser(Scanner scanner, Grammar g, Consumer<String> errorOutput) {
        this.scanner = scanner;
        this.g = g;
        this.errorOutput = errorOutput;
        tokens = new ArrayList<>();
    }

    public List<Token> getTokenList() {
        return this.tokens;
    }

    /**
     * Tokenizes the standard library and the source file.
     *
     * @return String which represents the abstract syntax tree,
     * at this stage it returns the string of tokens received from the scanner
     * @throws IOException
     */
    public boolean startParsing() throws IOException {

        injectLibraries();

        boolean pass = parse();

        return pass;
    }

    /**
     * Tokenizes the standard library line by line
     * Disables the trace temporarily so that the tokenizing process is never apart of output
     * Restores the trace flag to its previous value before returning.
     *
     * @throws java.io.IOException
     */
    public void injectLibraries() throws java.io.IOException {

        boolean temp = scanner.isTraceEnabled();
        scanner.setTraceEnabled(false);

        for (String foo : libraries) {

            parseLine(foo);

        }

        scanner.setTraceEnabled(temp);
    }

    /**
     * Drives the scanner to produce new tokens until the scanner reaches the end of it's input.
     *
     * @return - true when there were no error tokens produced, otherwise false
     * @throws java.io.IOException
     */
    public boolean parse() throws java.io.IOException {

        boolean pass = true;
        Token nextToken;
        Token lookaheadToken = scanner.nextToken();
        do {
            nextToken = lookaheadToken;
            lookaheadToken = scanner.nextToken();
            if (nextToken.token == Tokens.ERROR)
                pass = false;


        } while (nextToken.token != Tokens.ENDFILE);

        //System.out.println("Tokens: " + tokens);
        return pass;
    }

    /**
     * Redirects the scanner's Reader to a StringReader for the line given,
     * then parses the line, once the scanner finishes with that line,
     * the end of file token is dropped from the accumulated tokens,
     * then returns the scanner's Reader to the previous Reader object.
     *
     * @param line - String to be tokenized
     * @throws java.io.IOException
     */
    public void parseLine(String line) throws java.io.IOException {
        Reader fileReader = this.scanner.redirectReader(new StringReader(line));

        parse();
        this.tokens.remove(this.tokens.size() -1);//remove endfile token
        this.scanner.redirectReader(fileReader);
    }




    private AbstractSyntaxTreeNode loop_stmt(Set<Tokens> synch){

        match(Tokens.LOOP,synch);

        return null;
    }




    private void match(Tokens expected, Set<Tokens> synch){
       if(lookahead == expected)
           lookahead = scanner.nextToken().token;

        else
           syntaxError(synch);
        syntaxCheck(synch);
    }


    private void syntaxCheck(Set<Tokens> synch) {

    }

    private void syntaxError(Set<Tokens> synch) {
        throw new SyntaxError();
    }


}
