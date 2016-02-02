package parser;

import parser.grammar.*;
import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static parser.Tokens.*;
import static parser.grammar.FirstAndFollowSets.*;

/**
 *
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

    private Token lookaheadToken;
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

        AST ast = parse();

        return true;
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
    public AST parse() throws java.io.IOException {

        boolean pass = true;

        lookaheadToken = scanner.nextToken();
        lookahead = lookaheadToken.token;

        //System.out.println("Tokens: " + tokens);
        return program(new HashSet<>());
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

    private AST program(Set<Tokens> synch) {
        AST current = null;
        do {
            AST temp = declaration(synch);
            if( current != null )
                current.setNextNode(temp);
            current = temp;

        } while (FIRSTofDeclaration.contains(lookahead));
        return null;
    }

    
 




    private AST if_stmt(Set<Tokens> synch) {

        match(IF, synch);
        match(LPAREN, synch);
        Expression e = expression(synch);
        match(RPAREN, synch);
        Statement s = statement(synch);

        Statement elseStatement = null;
        if (lookahead == ELSE) {
            match(ELSE, synch);
            elseStatement = statement(synch);
        }

        return new IfStatement(e,s,elseStatement);
    }



    private AST declaration(Set<Tokens> synch) {

        if (lookahead == VOID) {
            match(VOID, synch);
            match(ID, synch);
            fun_dec_tail(synch);
        } else if (FIRSTofNonvoid_specifier.contains(lookahead)) {
            nonvoid_specifier(synch);
            match(ID, synch);
            dec_tail(synch);
        } else {
            //error
        }
        return null;
    }

    private AST nonvoid_specifier(Set<Tokens> synch) {
        if (lookahead == INT) match(INT, synch);
        if (lookahead == BOOL) match(BOOL, synch);
        return null;
    }

    private AST dec_tail(Set<Tokens> synch) {

        if (FIRSTofVar_dec_tail.contains(lookahead)) var_dec_tail(synch);
        if (FIRSTofFun_dec_tail.contains(lookahead)) fun_dec_tail(synch);

        return null;
    }

    private AST var_dec_tail(Set<Tokens> synch) {

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

    private AST var_name(Set<Tokens> synch) {

        match(ID, synch);

        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp(synch);
            match(RSQR, synch);
        }

        return null;
    }

    private AST fun_dec_tail(Set<Tokens> synch) {

        match(LPAREN, synch);
        params(synch);
        match(RPAREN, synch);
        //compound_stmt(synch);

        return null;
    }


    private AST params(Set<Tokens> synch) {
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

    private AST param(Set<Tokens> synch) {
        if (lookahead == REF) {
            match(REF, synch);
            nonvoid_specifier(synch);
            match(ID, synch);
        } else if (FIRSTofNonvoid_specifier.contains(lookahead)) {
            nonvoid_specifier(synch);
            match(ID, synch);
            if (lookahead == LSQR) {
                match(LSQR, synch);
                match(RSQR, synch);
            }
        }
        return null;
    }

    private Statement statement(Set<Tokens> synch) {
        if (FIRSTofId_stmt.contains(lookahead)) {
            id_stmt(synch);
        } else if (FIRSTofCompound_stmt.contains(lookahead)) {
            compound_stmt(synch);
        } else if (FIRSTofIf_stmt.contains(lookahead)) {
            if_stmt(synch);
        } else if (FIRSTofLoop_stmt.contains(lookahead)) {
            loop_stmt(synch);
        } else if (FIRSTofExit_stmt.contains(lookahead)) {
            exit_stmt(synch);
        } else if (FIRSTofContinue_stmt.contains(lookahead)) {
            continue_stmt(synch);
        } else if (FIRSTofReturn_stmt.contains(lookahead)) {
            return_stmt(synch);
        } else if (FIRSTofNull_stmt.contains(lookahead)) {
            null_stmt(synch);
        } else if (FIRSTofBranch_stmt.contains(lookahead)) {
            branch_stmt(synch);
        } else {
            //error
        }
        return null;
    }

    private AST id_stmt(Set<Tokens> synch) {
        match(ID, synch);
        id_stmt_tail(synch);
        return null;
    }

    private AST id_stmt_tail(Set<Tokens> synch) {
        if (FIRSTofAssign_stmt_tail.contains(lookahead)) assign_stmt_tail(synch);
        else if (FIRSTofCall_stmt_tail.contains(lookahead)) call_stmt_tail(synch);
        else ; //error

        return null;
    }

    private AST assign_stmt_tail(Set<Tokens> synch) {

        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp(synch);
            match(RSQR, synch);
        }

        match(ASSIGN, synch);
        expression(synch);
        match(SEMI, synch);

        return new NullStatement();
    }

    private AST call_stmt_tail(Set<Tokens> synch) {
        call_tail(synch);
        match(SEMI, synch);
        return null;
    }

    private AST call_tail(Set<Tokens> synch) {

        match(LPAREN, synch);
        if(FIRSTofArguments.contains(lookahead)) arguments(synch);
        match(RPAREN, synch);
        return null;
    }

    private AST arguments(Set<Tokens> synch) {

        expression(synch);
        while(lookahead == COMMA){
            match(COMMA, synch);
            expression(synch);
        }

        return null;
    }


    private AST compound_stmt(Set<Tokens> synch) {

        match(LCRLY, synch);

        if(FIRSTofNonvoid_specifier.contains(lookahead)){
            nonvoid_specifier(synch);
            match(ID, synch);
            var_dec_tail(synch);
        }
        do {
            statement(synch);
        } while(FIRSTofStatement.contains(lookahead));

        match(RCRLY, synch);

        return null;
    }

    private Statement loop_stmt(Set<Tokens> synch) {

        match(LOOP, synch);


        Statement first = statement(synch);
        Statement temp = first;
        while (FIRSTofStatement.contains(lookahead)) {
            Statement temp2 = statement(synch);
            temp.setNextNode(temp2);
            temp = temp2;
        }
        match(END, synch);


        return new LoopStatement(first);
    }

    private Statement exit_stmt(Set<Tokens> synch) {

        match(EXIT, synch);
        match(SEMI, synch);

        return new ExitStatement();
    }

    private AST continue_stmt(Set<Tokens> synch) {

        match(CONTINUE, synch);
        match(SEMI, synch);

        return new ContinueStatement();
    }

    private AST return_stmt(Set<Tokens> synch) {

        match(RETURN, synch);
        Expression returnValue = null;
        if (FIRSTofExpression.contains(lookahead))
            returnValue = expression(synch);
        match(SEMI, synch);

        return new ReturnStatement(returnValue);
    }


    private AST null_stmt(Set<Tokens> synch) {
        match(SEMI, synch);

        return null;
    }

    private AST branch_stmt(Set<Tokens> synch) {
        match(BRANCH, synch);
        match(LPAREN, synch);

        AddExpression addexp = add_exp(synch);

        match(RPAREN, synch);

        CaseStatement caseStmt = case_(synch);
        CaseStatement next = caseStmt;
        while (FIRSTofCase_stmt.contains(lookahead)) {
            CaseStatement temp = case_(synch);
            next.setNextNode(temp);
            next = temp;
        }

        match(END, synch);
        match(SEMI, synch);

        return new BranchStatement(addexp,caseStmt);
    }


    private CaseStatement case_(Set<Tokens> synch) {

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

    private Expression expression(Set<Tokens> synch) {
        add_exp(synch);
        if (FIRSTofRelop.contains(lookahead)) {
            relop(synch);
            add_exp(synch);
        }
        return null;
    }


    private AddExpression add_exp(Set<Tokens> synch) {
        boolean hasMinus = false;
        if (FIRSTofUMinus.contains(lookahead))
            uminus(synch);

        term(synch);

        if (FIRSTofAddOp.contains(lookahead)) {
            addop(synch);
            term(synch);
        }

        return null;
    }

    private AST term(Set<Tokens> synch) {
        factor(synch);

        if (FIRSTofMultop.contains(lookahead)) {
            multop(synch);
            factor(synch);
        }
        return null;
    }

    private AST factor(Set<Tokens> synch) {


        if (FIRSTofNid_factor.contains(lookahead)) {
            nid_factor(synch);
        } else {
            id_factor(synch);
        }
        return null;
    }

    private AST nid_factor(Set<Tokens> synch) {

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

    private AST id_factor(Set<Tokens> synch) {
        match(ID, synch);
        id_tail(synch);
        return null;
    }

    private AST id_tail(Set<Tokens> synch) {
        if (FIRSTofVar_tail.contains(lookahead)) {
            var_tail(synch);
        } else {
            call_tail(synch);

        }

        return null;
    }


    private AST var_tail(Set<Tokens> synch) {
        match(LSQR, synch);
        add_exp(synch);
        match(RSQR, synch);
        return null;
    }

    private AST relop(Set<Tokens> synch) {

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

    private AST addop(Set<Tokens> synch) {

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

    private AST multop(Set<Tokens> synch) {

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

    private AST uminus(Set<Tokens> synch) {
        match(MINUS, synch);
        return new MinusExpression();
    }


    private void match(Tokens expected, Set<Tokens> synch) {
        if (lookahead == expected) {
            lookaheadToken = scanner.nextToken();
            lookahead = lookaheadToken.token;
        }else
            syntaxError(synch);
        syntaxCheck(synch);
    }


    private void syntaxCheck(Set<Tokens> synch) {

    }

    private void syntaxError(Set<Tokens> synch) {
        throw new SyntaxError();
    }


}
