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

import static parser.TokenType.*;
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
    private final Consumer<String> lineTraceOutput;
    private List<Token> tokens;

    private Token lookaheadToken;
    private TokenType lookahead;
    private boolean traceEnabled;


    public Parser(Scanner scanner, Consumer<String> lineTraceOutput, Consumer<String> errorOutput) {
        this.scanner = scanner;
        this.lineTraceOutput = lineTraceOutput;
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
        //injectLibraries();

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
//        //debug
//        System.out.println(tokens.size());
//        tokens.forEach(System.out::println);
//        //debug
        this.tokens.remove(this.tokens.size() - 1); //remove endfile token
        this.scanner.redirectReader(fileReader);
    }

    private AST program(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering program");
        AST current = null;
        do {
            AST temp = declaration(synch);
            if( current != null )
                current.setNextNode(temp);
            current = temp;

        } while (FIRSTofDeclaration.contains(lookahead));
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving program");
        return null;
    }

    
 

    private AST if_stmt(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering if-stmt");
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

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving if-stmt");
        return new IfStatement(e,s,elseStatement);
    }



    private Declaration declaration(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering declaration");
        if (lookahead == VOID) {
            match(VOID, synch);
            Token token = lookaheadToken;
            match(ID, synch);
            FuncDeclarationTail funcDecTail = fun_dec_tail(synch);
            return new FuncDeclaration(Type.VOID,token,funcDecTail.getParams(),funcDecTail.getFuncBody());
        } else if (FIRSTofNonvoid_specifier.contains(lookahead)  ) {
            nonvoid_specifier(synch);
            match(ID, synch);
            dec_tail(synch);
        } else {
            //error
        }
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving declaration");
        return null;
    }

    private Type nonvoid_specifier(Set<TokenType> synch) {
        if(traceEnabled)lineTraceOutput.accept("\t\tEntering nonvoid-specifier");
        if (lookahead == INT) {
            match(INT, synch);
            lineTraceOutput.accept("\t\tLeaving nonvoid-specifier");
            return Type.INT;
        }
        if (lookahead == BOOL) {
            match(BOOL, synch);
            lineTraceOutput.accept("\t\tLeaving nonvoid-specifier");
            return Type.BOOL;
        }
        if(traceEnabled)throw new RuntimeException("probly remove this");
        return null;
    }

    private AST dec_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering dec-tail");
        if (FIRSTofVar_dec_tail.contains(lookahead)) var_dec_tail(synch);
        if (FIRSTofFun_dec_tail.contains(lookahead)) fun_dec_tail(synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving dec-tail");
        return null;
    }

    private AST var_dec_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering var-dec-tail");

        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp(synch);
            match(RSQR, synch);
        } else while (lookahead == COMMA) {
            match(COMMA, synch);
            var_name(synch);
        }
        match(SEMI, synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving var-dec-tail");
        return null;
    }

    private AST var_name(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering var-name");

        match(ID, synch);

        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp(synch);
            match(RSQR, synch);
        }

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving var-name");
        return null;
    }

    private FuncDeclarationTail fun_dec_tail(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering fun-dec-tail");
        match(LPAREN, synch);
        Declaration params = params(synch);
        match(RPAREN, synch);
        CompoundStatement statement = compound_stmt(synch);

        if(traceEnabled)  lineTraceOutput.accept("\t\tLeaving fun-dec-tail");

        return new FuncDeclarationTail(params,statement);
    }


    private Declaration params(Set<TokenType> synch) {
        if(traceEnabled)lineTraceOutput.accept("\t\tEntering params");
        if (lookahead == VOID) {
            match(VOID, synch);
        } else {
            param(synch);
            while (lookahead == COMMA) {
                match(COMMA, synch);
                param(synch);
            }
        }

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving params");
        return null;
    }

    private AST param(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering param");
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
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving param");
        return null;
    }

    private Statement statement(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering statement");
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
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving statement");
        return null;
    }

    private AST id_stmt(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-stmt");
        match(ID, synch);
        id_stmt_tail(synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving id-stmt");
        return null;
    }

    private AST id_stmt_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-stmt-tail");
        if (FIRSTofAssign_stmt_tail.contains(lookahead)) assign_stmt_tail(synch);
        else if (FIRSTofCall_stmt_tail.contains(lookahead)) call_stmt_tail(synch);
        else ; //error

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving id-stmt-tail");
        return null;
    }

    private AST assign_stmt_tail(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering assign-stmt-tail");
        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp(synch);
            match(RSQR, synch);
        }

        match(ASSIGN, synch);
        expression(synch);
        match(SEMI, synch);

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving assign-stmt-tail");
        return new NullStatement();
    }

    private AST call_stmt_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering call-stmt-tail");
        call_tail(synch);
        match(SEMI, synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving call-stmt-tail");
        return null;
    }

    private AST call_tail(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering call-tail");
        match(LPAREN, synch);
        if(FIRSTofArguments.contains(lookahead)) arguments(synch);
        match(RPAREN, synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving call-tail");
        return null;
    }

    private AST arguments(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering argument");

        expression(synch);
        while(lookahead == COMMA){
            match(COMMA, synch);
            expression(synch);
        }
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving argument");

        return null;
    }


    private CompoundStatement compound_stmt(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering compound-stmt");

        match(LCRLY, synch);

        Declaration declaration = null;

        if(FIRSTofNonvoid_specifier.contains(lookahead)){

            Type decType = nonvoid_specifier(synch);
            Token IDToken = lookaheadToken;
            match(ID, synch);

            var_dec_tail(synch);

            declaration = new Declaration(decType,IDToken);
        }

        Statement first = null;

        do {
            Statement temp = statement(synch);

            if( first == null )
                first = temp;
            else
                first.setNextNode(temp);
        } while(FIRSTofStatement.contains(lookahead));

        match(RCRLY, synch);

        if(traceEnabled)lineTraceOutput.accept("\t\tLeaving compound-stmt");
        return new CompoundStatement(declaration,first);
    }

    private Statement loop_stmt(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering loop-stmt");

        match(LOOP, synch);


        Statement first = statement(synch);
        Statement temp = first;
        while (FIRSTofStatement.contains(lookahead)) {
            Statement temp2 = statement(synch);
            temp.setNextNode(temp2);
            temp = temp2;
        }
        match(END, synch);


        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving loop-stmt");
        return new LoopStatement(first);
    }

    private Statement exit_stmt(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering exit-stmt");
        match(EXIT, synch);
        match(SEMI, synch);

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving exit-stmt");
        return new ExitStatement();
    }

    private AST continue_stmt(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering continue-stmt");
        match(CONTINUE, synch);
        match(SEMI, synch);

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving continue-stmt");
        return new ContinueStatement();
    }

    private AST return_stmt(Set<TokenType> synch) {

        if(traceEnabled) lineTraceOutput.accept("\t\tEntering return-stmt");
        match(RETURN, synch);
        Expression returnValue = null;
        if (FIRSTofExpression.contains(lookahead))
            returnValue = expression(synch);
        match(SEMI, synch);

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving return-stmt");
        return new ReturnStatement(returnValue);
    }


    private AST null_stmt(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering null-stmt");
        match(SEMI, synch);

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving null-stmt");
        return null;
    }

    private AST branch_stmt(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering branch-stmt");
        match(BRANCH, synch);
        match(LPAREN, synch);

        AddExpression addexp = add_exp(synch);

        match(RPAREN, synch);

        CaseStatement caseStmt = case_stmt(synch);
        CaseStatement next = caseStmt;
        while (FIRSTofCase_stmt.contains(lookahead)) {
            CaseStatement temp = case_stmt(synch);
            next.setNextNode(temp);
            next = temp;
        }

        match(END, synch);
        match(SEMI, synch);

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving branch-stmt");
        return new BranchStatement(addexp,caseStmt);
    }


    private CaseStatement case_stmt(Set<TokenType> synch) {
        if(traceEnabled)lineTraceOutput.accept("\t\tEntering case-stmt");
        Statement statement;
        Token t = null;
        if (lookahead == CASE) {
            match(CASE, synch);
            t = lookaheadToken;
            match(NUM, synch);
            match(COLON, synch);
            statement = statement(synch);
        } else {
            match(DEFAULT, synch);
            match(COLON, synch);
            statement = statement(synch);
        }
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving case-stmt");

        return new CaseStatement(t, statement);
    }

    private Expression expression(Set<TokenType> synch) {
        if(traceEnabled)lineTraceOutput.accept("\t\tEntering expression");

        AddExpression addExpression = add_exp(synch);

        if (FIRSTofRelop.contains(lookahead)) {

            TokenType relop = relop(synch);
            AddExpression addExp2 = add_exp(synch);

            return new Expression(addExpression,relop,addExp2);
        }
        if(traceEnabled)lineTraceOutput.accept("\t\tLeaving expression");
        return new Expression(addExpression,null,null);
    }


    private AddExpression add_exp(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering add-exp");
        boolean hasMinus = false;
        if (FIRSTofUMinus.contains(lookahead))
            uminus(synch);

        term(synch);

        if (FIRSTofAddOp.contains(lookahead)) {
            addop(synch);
            term(synch);
        }

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving add-exp");
        return null;
    }

    private AST term(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering term");
        factor(synch);

        if (FIRSTofMultop.contains(lookahead)) {
            multop(synch);
            factor(synch);
        }
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving term");
        return null;
    }

    private AST factor(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering factor");


        if (FIRSTofNid_factor.contains(lookahead)) {
            nid_factor(synch);
        } else {
            id_factor(synch);
        }
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving factor");
        return null;
    }

    private AST nid_factor(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering nid-factor");

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

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving nid-factor");
        return null;
    }

    private AST id_factor(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-factor");
        match(ID, synch);
        id_tail(synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving id-factor");
        return null;
    }

    private AST id_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-tail");
        if (FIRSTofVar_tail.contains(lookahead)) {
            var_tail(synch);
        } else {
            call_tail(synch);

        }

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving id-tail");
        return null;
    }


    private AST var_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering var-tail");
        match(LSQR, synch);
        add_exp(synch);
        match(RSQR, synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving var-tail");
        return null;
    }

    private TokenType relop(Set<TokenType> synch) {
        if(traceEnabled)lineTraceOutput.accept("\t\tEntering relop");

        switch (lookahead) {
            case LTEQ:
                match(LTEQ, synch);
                return LTEQ;
            case LT:
                match(LT, synch);
                return LT;
            case GT:
                match(GT, synch);
                return GT;
            case GTEQ:
                match(GTEQ, synch);
                return GTEQ;
            case EQ:
                match(EQ, synch);
                return EQ;
            case NEQ:
                match(NEQ, synch);
                return NEQ;
        }
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving relop");

        return null;
    }

    private AST addop(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering addop");

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

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving addop");
        return null;
    }

    private AST multop(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering multop");

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

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving multop");
        return null;
    }

    private AST uminus(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering unimus");
        match(MINUS, synch);
        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving unimus");
        return new MinusExpression();
    }

    private void match(TokenType expected, Set<TokenType> synch) {
        if (lookahead == expected) {
            if(traceEnabled) lineTraceOutput.accept("\t\tMatched " + expected.toString());
            lookaheadToken = scanner.nextToken();
            this.tokens.add(lookaheadToken);
            lookahead = lookaheadToken.token;
        }else
            syntaxError(synch);
        syntaxCheck(synch);
    }

    private void syntaxCheck(Set<TokenType> synch) {

    }

    private void syntaxError(Set<TokenType> synch) {
        errorOutput.accept("\t\tSyntax Error");
        //throw new SyntaxError();
    }


    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public boolean isTraceEnabled() {
        return traceEnabled;
    }
}
