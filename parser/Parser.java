package parser;

import parser.grammar.*;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.ParamDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.declarations.temps.DecTail;
import parser.grammar.declarations.temps.FuncDeclarationTail;
import parser.grammar.declarations.temps.VarDecTail;
import parser.grammar.declarations.temps.VarName;
import parser.grammar.expressions.*;
import parser.grammar.statements.*;
import scanner.Scanner;
import scanner.Token;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static parser.TokenType.*;
import static parser.grammar.FirstAndFollowSets.*;

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
    public ASTNode startParsing() throws IOException {
        //injectLibraries();
        ASTNode astNode;
        try{
            astNode = parse();
        }catch(Exception e){
            return null;
        }

        return astNode;
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
    public ASTNode parse() throws java.io.IOException {

        lookaheadToken = scanner.nextToken();
        lookahead = lookaheadToken.token;

        return program(new HashSet<>(Arrays.asList(new TokenType[]{ENDFILE})));
    }


    String currentLine;
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
        java.util.Scanner supplier = new java.util.Scanner(line);
        currentLine = supplier.nextLine();
        Supplier<Integer> fileReader = this.scanner.redirectReader(()->{
            if (currentLine.length() == 0 )
                return -1;
            int c = currentLine.charAt(0);
            currentLine = currentLine.substring(1,currentLine.length());
            return c;
        });

        parse();

        this.tokens.remove(this.tokens.size() - 1); //remove endfile token
        this.scanner.redirectReader(fileReader);
    }


    private ASTNode program(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering program");

        ASTNode first = null;
        ASTNode current = null;
        do {
            ASTNode temp = declaration(synch);
            if (current != null)
                current.setNextNode(temp);
            if( first == null )
                first = temp;
            current = temp;

        } while (FIRSTofDeclaration.contains(lookahead));

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving program");
        return first;
    }

    /**
     * THE DISAMBIGUATION RULE:
     * The if-stmt production rule (20) causes the `dangling else' ambiguity in the specified
     * grammar. This is intentional, as it makes the grammar simpler. The disambiguation rule is that
     * each else matches the closest preceding if at the same level of the nesting of braces. For in-
     * stance, if (E1) if (E2) C1 else C2 means the same as if (E1) {if (E2) C1 else C2}.
     * We must write if (E1) {if (E2) C1} else C2 if we wish the else to match the first if.
     *
     * @param synch
     * @return
     */
    private IfStatement if_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering if-stmt");

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

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving if-stmt");
        return new IfStatement(e, s, elseStatement);
    }

    private Declaration declaration(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering declaration");
        //
        if (lookahead == VOID) {//function declaration
            match(VOID, synch);
            Token token = lookaheadToken;
            match(ID, synch);
            FuncDeclarationTail funcDecTail = fun_dec_tail(synch);
            return new FuncDeclaration(Type.VOID, token, funcDecTail.getParams(), funcDecTail.getFuncBody());
        } else if (FIRSTofNonvoid_specifier.contains(lookahead)) {//variable declaration or function declaration
            Type type = nonvoid_specifier(synch);
            Token token = lookaheadToken;
            match(ID, synch);
            DecTail decTail = dec_tail(synch);
            if (decTail instanceof VarDecTail) {
                VarDecTail varDecTail = (VarDecTail) decTail;
                return varDecTail.toVarDeclarations(type, token);
            } else if (decTail instanceof FuncDeclarationTail) {
                FuncDeclarationTail funcDecTail = (FuncDeclarationTail) decTail;
                return new FuncDeclaration(type, token, funcDecTail.getParams(), funcDecTail.getFuncBody());
            }
            return new VarDeclaration(type, token);
        } else {
            //error
        }
        //
        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving declaration");
        return null;
    }

    private Type nonvoid_specifier(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering nonvoid-specifier");
        if (lookahead == INT) {
            match(INT, synch);
            lineTraceOutput.accept("\t\tLeaving nonvoid-specifier");
            return Type.INT;
        }
        if (lookahead == BOOL) {
            match(BOOL, synch);
            if (traceEnabled) lineTraceOutput.accept("\t\tLeaving nonvoid-specifier");
            return Type.BOOL;
        }
        if (traceEnabled) throw new RuntimeException("probly remove this");
        return null;
    }

    private DecTail dec_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering dec-tail");

        DecTail decTail;
        if (FIRSTofVar_dec_tail.contains(lookahead)) {
            decTail = var_dec_tail(synch);
        } else {/*if (FIRSTofFun_dec_tail.contains(lookahead))*/
            decTail = fun_dec_tail(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving dec-tail");
        return decTail;
    }

    private VarDecTail var_dec_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering var-dec-tail");

        AddExpression add_exp = null;
        List<VarName> varNames = new ArrayList<>();

        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp = add_exp(synch);
            match(RSQR, synch);
        } else while (lookahead == COMMA) {
            match(COMMA, synch);
            varNames.add(var_name(synch));
        }
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving var-dec-tail");
        return new VarDecTail(add_exp, varNames);
    }

    private VarName var_name(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering var-name");

        Token IDToken = lookaheadToken;
        match(ID, synch);

        AddExpression add_exp = null;
        if (lookahead == LSQR) {
            match(LSQR, synch);
            add_exp = add_exp(synch);
            match(RSQR, synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving var-name");
        return new VarName(IDToken, add_exp);
    }

    private FuncDeclarationTail fun_dec_tail(Set<TokenType> synch) {

        if (traceEnabled) lineTraceOutput.accept("\t\tEntering fun-dec-tail");
        match(LPAREN, synch);
        ParamDeclaration params = params(synch);
        match(RPAREN, synch);
        CompoundStatement statement = compound_stmt(synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving fun-dec-tail");

        return new FuncDeclarationTail(params, statement);
    }


    private ParamDeclaration params(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering params");
        //
        if (lookahead == VOID) {
            match(VOID, synch);
            if (traceEnabled) lineTraceOutput.accept("\t\tLeaving params");
            return ParamDeclaration.voidParam;
        } else {
            ParamDeclaration head = param(synch);
            ParamDeclaration current = head;
            ParamDeclaration temp;
            while (lookahead == COMMA) {
                match(COMMA, synch);
                temp = param(synch);
                current.setNextNode(temp);
                current = temp;
            }
            if (traceEnabled) lineTraceOutput.accept("\t\tLeaving params");
            return head;//possible bug, returning here when there could have been an error
        }
        // if (traceEnabled) lineTraceOutput.accept("\t\tLeaving params");
        // return null;
    }

    private ParamDeclaration param(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering param");
        boolean isReference = lookahead == REF;
        boolean isArray = false;
        if (isReference) {
            match(REF, synch);
            Type type = nonvoid_specifier(synch);
            Token token = lookaheadToken;
            match(ID, synch);
            return new ParamDeclaration(type, token, isArray, isReference);
        } else if (FIRSTofNonvoid_specifier.contains(lookahead)) {
            Type type = nonvoid_specifier(synch);
            Token token = lookaheadToken;
            match(ID, synch);
            if (lookahead == LSQR) {
                match(LSQR, synch);
                match(RSQR, synch);
                isArray = true;
            }
            return new ParamDeclaration(type, token, isArray, isReference);
        }
        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving param");
        return null;
    }

    private Statement statement(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering statement");

        Statement s = null;
        if (FIRSTofId_stmt.contains(lookahead)) {
            s = id_stmt(synch);
        } else if (FIRSTofCompound_stmt.contains(lookahead)) {
            s = compound_stmt(synch);
        } else if (FIRSTofIf_stmt.contains(lookahead)) {
            s = if_stmt(synch);
        } else if (FIRSTofLoop_stmt.contains(lookahead)) {
            s = loop_stmt(synch);
        } else if (FIRSTofExit_stmt.contains(lookahead)) {
            s = exit_stmt(synch);
        } else if (FIRSTofContinue_stmt.contains(lookahead)) {
            s = continue_stmt(synch);
        } else if (FIRSTofReturn_stmt.contains(lookahead)) {
            s = return_stmt(synch);
        } else if (FIRSTofNull_stmt.contains(lookahead)) {
            s = null_stmt(synch);
        } else if (FIRSTofBranch_stmt.contains(lookahead)) {
            s = branch_stmt(synch);
        } else {
            errorOutput.accept("\t\tExpecting statement!");
            //error
        }
        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving statement");
        return s;
    }

    private IdStatement id_stmt(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-stmt");

        Token idToken = lookaheadToken;
        match(ID, synch);

        StatementTail id_stmt_tail = id_stmt_tail(synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving id-stmt");
        return new IdStatement(idToken,id_stmt_tail);
    }

    private StatementTail id_stmt_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-stmt-tail");

        StatementTail statementTail;
        if (FIRSTofAssign_stmt_tail.contains(lookahead))
            statementTail = assign_stmt_tail(synch);
        else
            statementTail = call_stmt_tail(synch);


        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving id-stmt-tail");
        return statementTail;
    }

    private StatementTail assign_stmt_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering assign-stmt-tail");

        AddExpression addExpression = null;

        if (lookahead == LSQR) {
            match(LSQR, synch);
            addExpression = add_exp(synch);
            match(RSQR, synch);
        }

        match(ASSIGN, synch);
        Expression exp = expression(synch);
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving assign-stmt-tail");
        return new AssignStatementTail(addExpression,exp);
    }

    private CallStatementTail call_stmt_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering call-stmt-tail");

        Expression call_tail = call_tail(synch);
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving call-stmt-tail");
        return new CallStatementTail(call_tail);
    }

    private Expression call_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering call-tail");

        Expression args = null;
        match(LPAREN, synch);
        if (FIRSTofArguments.contains(lookahead))
            args = arguments(synch);
        match(RPAREN, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving call-tail");
        return args;
    }

    private Expression arguments(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering argument");

        Expression exp = expression(synch);

        Expression tmp = exp;
        while (lookahead == COMMA) {
            match(COMMA, synch);
            Expression tmp2 = expression(synch);
            tmp.setNextNode(tmp2);
            tmp = tmp2;
        }
        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving argument");

        return exp;
    }


    private CompoundStatement compound_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering compound-stmt");

        match(LCRLY, synch);

        Declaration firstDec = null;
        Declaration tempDec = null;
        while (FIRSTofNonvoid_specifier.contains(lookahead)) {

            Type decType = nonvoid_specifier(synch);
            Token IDToken = lookaheadToken;
            match(ID, synch);

            var_dec_tail(synch);

            Declaration tempDec2 = new Declaration(decType, IDToken);
            if( firstDec == null ) {
                firstDec = tempDec2;
                tempDec = tempDec2;
            }else {
                tempDec.setNextNode(tempDec2);
                tempDec = tempDec2;
            }
        }

        Statement first = null;
        Statement tempStmt = null;
        do {
            Statement temp = statement(synch);

            if (first == null) {
                first = temp;
                tempStmt = temp;
            }else {
                tempStmt.setNextNode(temp);
                tempStmt = temp;
            }
        } while (FIRSTofStatement.contains(lookahead));

        match(RCRLY, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving compound-stmt");
        return new CompoundStatement(firstDec, first);
    }

    private Statement loop_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering loop-stmt");

        match(LOOP, synch);

        Statement first = statement(synch);
        Statement temp = first;
        while (FIRSTofStatement.contains(lookahead)) {
            Statement temp2 = statement(synch);
            temp.setNextNode(temp2);
            temp = temp2;
        }
        match(END, synch);
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving loop-stmt");
        return new LoopStatement(first);
    }

    private Statement exit_stmt(Set<TokenType> synch) {

        if (traceEnabled) lineTraceOutput.accept("\t\tEntering exit-stmt");
        match(EXIT, synch);
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving exit-stmt");
        return new ExitStatement();
    }

    private Statement continue_stmt(Set<TokenType> synch) {

        if (traceEnabled) lineTraceOutput.accept("\t\tEntering continue-stmt");
        match(CONTINUE, synch);
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving continue-stmt");
        return new ContinueStatement();
    }

    private ReturnStatement return_stmt(Set<TokenType> synch) {

        if (traceEnabled) lineTraceOutput.accept("\t\tEntering return-stmt");
        match(RETURN, synch);
        Expression returnValue = null;
        if (FIRSTofExpression.contains(lookahead))
            returnValue = expression(synch);
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving return-stmt");
        return new ReturnStatement(returnValue);
    }


    private Statement null_stmt(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering null-stmt");
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving null-stmt");
        return new NullStatement();
    }

    private BranchStatement branch_stmt(Set<TokenType> synch) {
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

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving branch-stmt");
        return new BranchStatement(addexp, caseStmt);
    }


    private CaseStatement case_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering case-stmt");

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

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving case-stmt");
        return new CaseStatement(t, statement);
    }

    private Expression expression(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering expression");

        AddExpression addExpression = add_exp(synch);

        if (FIRSTofRelop.contains(lookahead)) {

            TokenType relop = relop(synch);
            AddExpression addExp2 = add_exp(synch);

            return new Expression(addExpression, relop, addExp2);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving expression");
        return new Expression(addExpression, null, null);
    }


    private AddExpression add_exp(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering add-exp");

        boolean minusExpr = false;

        if (FIRSTofUMinus.contains(lookahead)) {
            uminus(synch);
            minusExpr = true;
        }

        Term term = term(synch);
        TokenType addop = null;
        Term term2 = null;
        if (FIRSTofAddOp.contains(lookahead)) {
            addop = addop(synch);
            term2 = term(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving add-exp");
        return new AddExpression(minusExpr, term, addop, term2);
    }

    private Term term(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering term");

        ASTNode factor = factor(synch);

        TokenType multop = null;
        ASTNode factor2 = null;


        if (FIRSTofMultop.contains(lookahead)) {
            multop = multop(synch);
            factor2 = factor(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving term");
        return new Term(factor, multop, factor2);
    }

    private ASTNode factor(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering factor");

        ASTNode factor;
        if (FIRSTofNid_factor.contains(lookahead)) {
            factor = nid_factor(synch);
        } else {
            factor = id_factor(synch);
        }

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving factor");
        return factor;
    }

    private ASTNode nid_factor(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering nid-factor");

        ASTNode nidFactor = null;
        switch (lookahead) {
            case NOT:
                match(NOT, synch);
                nidFactor = new NotNidFactor(factor(synch));
                break;
            case LPAREN:
                match(LPAREN, synch);
                nidFactor = expression(synch);
                match(RPAREN, synch);
                break;
            case NUM:
                Token numToken = lookaheadToken;
                match(NUM, synch);
                nidFactor = new NUM_NidFactor((Integer) numToken.attrValue);
                break;
            case BLIT:
                Token blitToken = lookaheadToken;
                match(BLIT, synch);
                nidFactor = new BLIT_NidFactor((Integer) blitToken.attrValue);
                break;
        }

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving nid-factor");
        return nidFactor;
    }

    private Factor id_factor(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-factor");

        Token IDToken = lookaheadToken;
        match(ID, synch);
        ASTNode idTail = id_tail(synch);

        if(traceEnabled) lineTraceOutput.accept("\t\tLeaving id-factor");
        return new IdFactor(IDToken,idTail);
    }

    private ASTNode id_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering id-tail");

        ASTNode idTail;
        if (FIRSTofCall_tail.contains(lookahead)) {
            idTail = call_tail(synch);
        } else {
            idTail = var_tail(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving id-tail");
        return idTail;
    }


    private ASTNode var_tail(Set<TokenType> synch) {
        if(traceEnabled) lineTraceOutput.accept("\t\tEntering var-tail");

        AddExpression add_exp = null;
        if( lookahead == LSQR ) {
            match(LSQR, synch);
            add_exp = add_exp(synch);
            match(RSQR, synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving var-tail");
        return add_exp;
    }

    private TokenType relop(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering relop");

        TokenType tt = lookahead;
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

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving relop");
        return tt;
    }

    private TokenType addop(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering addop");

        TokenType tokenType = lookahead;
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

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving addop");
        return tokenType;
    }

    private TokenType multop(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering multop");

        TokenType multop = lookahead;
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

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving multop");
        return multop;
    }

    private MinusExpression uminus(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering unimus");

        match(MINUS, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving unimus");
        return new MinusExpression();
    }

    private void match(TokenType expected, Set<TokenType> synch) {
        if (lookahead == expected) {
            if (traceEnabled) lineTraceOutput.accept("\t\tMatched " + expected.toString());

            lookaheadToken = scanner.nextToken();

            this.tokens.add(lookaheadToken);
            lookahead = lookaheadToken.token;
        } else
            syntaxError(synch);
        syntaxCheck(synch);
    }

    private void syntaxCheck(Set<TokenType> synch) {

    }

    private void syntaxError(Set<TokenType> synch) {
        errorOutput.accept("\t\tSyntax Error");
        throw new SyntaxError();
    }


    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public boolean isTraceEnabled() {
        return traceEnabled;
    }
}
