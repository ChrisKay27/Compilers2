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

import static parser.FirstAndFollowSets.*;
import static parser.Tokens.*;

/**
 * Created by Chris on 1/9/2016.
 */
public class Parser {

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

    public Parser(Scanner scanner, Consumer<String> errorOutput) {
        this.scanner = scanner;
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
        this.tokens.remove(this.tokens.size() - 1);//remove endfile token
        this.scanner.redirectReader(fileReader);
    }

    private AbstractSyntaxTreeNode program(Set<Tokens> synch) {
        AbstractSyntaxTreeNode current;
        do {
            declaration(synch);
        } while (FIRSTofDeclaration.contains(lookahead));
        return null;
    }


    private AbstractSyntaxTreeNode if_stmt(Set<Tokens> synch) {

        match(IF, synch);
        match(LPAREN, synch);
        expression(synch);
        match(RPAREN, synch);
        statement(synch);

        if (lookahead == ELSE) {
            match(ELSE, synch);
            statement(synch);
        }

        return null;
    }

    private void statement(Set<Tokens> synch) {
    }

    private AbstractSyntaxTreeNode declaration(Set<Tokens> synch) {

        if (lookahead == VOID) {
            match(VOID, synch);
            match(ID, synch);
            fun_dec_tail(synch);
        } else if (FIRSTofNonvoidspecifier.contains(lookahead)) {
            nonvoid_specifier(synch);
            match(ID, synch);
            dec_tail(synch);
        } else {
            //error
        }
        return null;
    }

    private AbstractSyntaxTreeNode nonvoid_specifier(Set<Tokens> synch) {
        if (lookahead == INT) match(INT, synch);
        if (lookahead == BOOL) match(BOOL, synch);
        return null;
    }

    private AbstractSyntaxTreeNode dec_tail(Set<Tokens> synch) {

        if (FIRSTofVarDectail.contains(lookahead)) var_dec_tail(synch);
        if (FIRSTofFundectail.contains(lookahead)) fun_dec_tail(synch);

        return null;
    }

    private AbstractSyntaxTreeNode var_dec_tail(Set<Tokens> synch) {

        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp(synch);
            match(RSQR, synch);
        } else while (lookahead == COMMA) {
            match(COMMA, synch);
            var_name(synch);
        }
        match(SEMI, synch);
        return null;
    }

    private AbstractSyntaxTreeNode var_name(Set<Tokens> synch) {

        match(ID, synch);

        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp(synch);
            match(RSQR, synch);
        }

        return null;
    }

    private AbstractSyntaxTreeNode fun_dec_tail(Set<Tokens> synch) {

        match(LPAREN, synch);
        params(synch);
        match(RPAREN, synch);
        //compound_stmt(synch);

        return null;
    }


    private AbstractSyntaxTreeNode params(Set<Tokens> synch) {
        if (lookahead == VOID) {
            match(VOID, synch);
        } else {
            param(synch);
            while (lookahead == COMMA) {
                match(COMMA, synch);
                param(synch);
            }
        }

        return null;
    }

    private AbstractSyntaxTreeNode param(Set<Tokens> synch) {
        if (lookahead == REF) {
            match(REF, synch);
            nonvoid_specifier(synch);
            match(ID, synch);
        } else if (FIRSTofNonvoidspecifier.contains(lookahead)) {
            nonvoid_specifier(synch);
            match(ID, synch);
            if (lookahead == LSQR) {
                match(LSQR, synch);
                match(RSQR, synch);
            }
        }
        return null;
    }

    private AbstractSyntaxTreeNode call_tail(Set<Tokens> synch) {


        return null;
    }

    private AbstractSyntaxTreeNode loop_stmt(Set<Tokens> synch) {

        match(LOOP, synch);

        statement(synch);
        while (FIRSTofStatement.contains(lookahead)) {
            statement(synch);
        }
        match(END, synch);

        return null;
    }

    private AbstractSyntaxTreeNode exit_stmt(Set<Tokens> synch) {

        match(EXIT, synch);
        match(SEMI, synch);

        return null;
    }

    private AbstractSyntaxTreeNode continue_stmt(Set<Tokens> synch) {

        match(CONTINUE, synch);
        match(SEMI, synch);

        return null;
    }

    private AbstractSyntaxTreeNode return_stmt(Set<Tokens> synch) {

        match(RETURN, synch);
        if (FIRSTofExpression.contains(lookahead))
            expression(synch);
        match(SEMI, synch);

        return null;
    }


    private AbstractSyntaxTreeNode null_stmt(Set<Tokens> synch) {
        match(SEMI, synch);

        return null;
    }

    private AbstractSyntaxTreeNode branch_stmt(Set<Tokens> synch) {
        match(BRANCH, synch);
        match(LPAREN, synch);

        add_exp(synch);

        match(RPAREN, synch);

        case_(synch);
        while (FIRSTofCase.contains(lookahead)) {
            case_(synch);
        }

        match(END, synch);
        match(SEMI, synch);

        return null;
    }


    private AbstractSyntaxTreeNode case_(Set<Tokens> synch) {

        if (lookahead == CASE) {
            match(CASE, synch);
            match(NUM, synch);
            match(COLON, synch);
            statement(synch);
        } else {
            match(DEFAULT, synch);
            match(COLON, synch);
            statement(synch);
        }

        return null;
    }

    private AbstractSyntaxTreeNode expression(Set<Tokens> synch) {
        add_exp(synch);
        if (FIRSTofRelop.contains(lookahead)) {
            relop(synch);
            add_exp(synch);
        }
        return null;
    }


    private AbstractSyntaxTreeNode add_exp(Set<Tokens> synch) {

        if (FIRSTofUMinus.contains(lookahead)) {
            uminus(synch);
            term(synch);
            if (FIRSTofAddOp.contains(lookahead)) {
                addop(synch);
                term(synch);
            }
        }
        return null;
    }

    private AbstractSyntaxTreeNode term(Set<Tokens> synch) {
        factor(synch);

        if (FIRSTofMultop.contains(lookahead)) {
            multop(synch);
            factor(synch);
        }
        return null;
    }

    private AbstractSyntaxTreeNode factor(Set<Tokens> synch) {


        if (FIRSTofNidFactor.contains(lookahead)) {
            nid_factor(synch);
        } else {
            id_factor(synch);
        }
        return null;
    }

    private AbstractSyntaxTreeNode nid_factor(Set<Tokens> synch) {

        switch (lookahead) {
            case NOT:
                match(NOT, synch);
                factor(synch);
                break;
            case LPAREN:
                match(LPAREN, synch);
                expression(synch);
                match(RPAREN, synch);
                break;
            case NUM:
                match(NUM, synch);
                break;
            case BLIT:
                match(BLIT, synch);
                break;
        }

        return null;
    }

    private AbstractSyntaxTreeNode id_factor(Set<Tokens> synch) {
        match(ID, synch);
        id_tail(synch);
        return null;
    }

    private AbstractSyntaxTreeNode id_tail(Set<Tokens> synch) {
        if (FIRSTofVarTail.contains(lookahead)) {
            var_tail(synch);
        } else {
            call_tail(synch);

        }

        return null;
    }


    private AbstractSyntaxTreeNode var_tail(Set<Tokens> synch) {
        match(LSQR, synch);
        add_exp(synch);
        match(RSQR, synch);
        return null;
    }

    private AbstractSyntaxTreeNode relop(Set<Tokens> synch) {

        switch (lookahead) {
            case LTEQ:
                match(LTEQ, synch);
                break;
            case LT:
                match(LT, synch);
                break;
            case GT:
                match(GT, synch);
                break;
            case GTEQ:
                match(GTEQ, synch);
                break;
            case EQ:
                match(EQ, synch);
                break;
            case NEQ:
                match(NEQ, synch);
                break;
        }

        return null;
    }

    private AbstractSyntaxTreeNode addop(Set<Tokens> synch) {

        switch (lookahead) {
            case PLUS:
                match(PLUS, synch);
                break;
            case MINUS:
                match(MINUS, synch);
                break;
            case OR:
                match(OR, synch);
                break;
            case ORELSE:
                match(ORELSE, synch);
                break;
        }

        return null;
    }

    private AbstractSyntaxTreeNode multop(Set<Tokens> synch) {

        switch (lookahead) {
            case MULT:
                match(MULT, synch);
                break;
            case DIV:
                match(DIV, synch);
                break;
            case MOD:
                match(MOD, synch);
                break;
            case AND:
                match(AND, synch);
                break;
            case ANDTHEN:
                match(ANDTHEN, synch);
                break;
        }

        return null;
    }

    private AbstractSyntaxTreeNode uminus(Set<Tokens> synch) {
        match(MINUS, synch);
        return null;
    }


    private void match(Tokens expected, Set<Tokens> synch) {
        if (lookahead == expected)
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
