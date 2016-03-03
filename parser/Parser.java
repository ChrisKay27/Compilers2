package parser;

import parser.grammar.ASTNode;
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
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static parser.TokenType.*;
import static parser.grammar.FirstSets.*;

/**
 * Created by Chris on 1/9/2016.
 */
public class Parser {

//    // standard library
//    private static String[] libraries = {
//            "int readint(void);",
//            "void writeint(int x);",
//            "bool readbool(void);",
//            "void writebool(bool x);"
//    };
//    /**
//     * Tokenizes the standard library line by line
//     * Disables the trace temporarily so that the tokenizing process is never apart of output
//     * Restores the trace flag to its previous value before returning.
//     *
//     * @throws java.io.IOException
//     */
//    public void injectLibraries() throws java.io.IOException {
//
//        boolean temp = scanner.isTraceEnabled();
//        scanner.setTraceEnabled(false);
//
//        for (String foo : libraries) {
//            parseLine(foo);
//        }
//
//        scanner.setTraceEnabled(temp);
//    }

    private final Scanner scanner;
    private final Consumer<String> errorOutput;
    private final Consumer<String> lineTraceOutput;
    private List<Token> tokens;

    private Token lookaheadToken;
    private TokenType lookahead;
    private boolean traceEnabled;
    private boolean syntaxError;


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
        try {
            astNode = parse();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (!syntaxError)
            return astNode;
        return null;
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
        Supplier<Integer> fileReader = this.scanner.redirectReader(() -> {
            if (currentLine.length() == 0)
                return -1;
            int c = currentLine.charAt(0);
            currentLine = currentLine.substring(1, currentLine.length());
            return c;
        });

        parse();

        this.tokens.remove(this.tokens.size() - 1); //remove endfile token
        this.scanner.redirectReader(fileReader);
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

        return program(new HashSet<>(Arrays.asList(new TokenType[] {ENDFILE})));
    }

    /**
     * 1.	program → {|declaration|}+
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private ASTNode program(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering program");

        ASTNode first = null;
        ASTNode current = null;
        do {
            ASTNode temp = declaration(union(synch, FIRSTofDeclaration));
            if (current != null)
                current.setNextNode(temp);
            if (first == null)
                first = temp;
            current = temp;

        } while (FIRSTofDeclaration.contains(lookahead));

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving program");
        return first;
    }


    /**
     * 18.	if-stmt → if ( expression ) statement [ else statement ]
     * <p>
     * THE DISAMBIGUATION RULE:
     * The if-stmt production rule (20) causes the `dangling else' ambiguity in the specified
     * grammar. This is intentional, as it makes the grammar simpler. The disambiguation rule is that
     * each else matches the closest preceding if at the same level of the nesting of braces. For in-
     * stance, if (E1) if (E2) C1 else C2 means the same as if (E1) {if (E2) C1 else C2}.
     * We must write if (E1) {if (E2) C1} else C2 if we wish the else to match the first if.
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private IfStatement if_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering if-stmt");

        match(IF, union(LPAREN, synch));
        match(LPAREN, union(FIRSTofExpression, synch));
        Expression e = expression(union(RPAREN, synch));
        match(RPAREN, union(FIRSTofStatement, synch));

        Statement s = statement(union(ELSE, synch));

        Statement elseStatement = null;
        if (lookahead == ELSE) {
            match(ELSE, union(FIRSTofStatement, synch));
            elseStatement = statement(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving if-stmt");
        return new IfStatement(e, s, elseStatement);
    }

    /**
     * 2.	declaration → void ID fun-dec-tail | nonvoid-specifier ID dec-tail
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Declaration declaration(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering declaration");
        //
        if (lookahead == VOID) {
            //void function declaration
            match(VOID, union(ID, synch));
            Token token = lookaheadToken;
            match(ID, union(FIRSTofFun_dec_tail, synch));
            FuncDeclarationTail funcDecTail = fun_dec_tail(synch);
            return new FuncDeclaration(Type.VOID, token, funcDecTail.getParams(), funcDecTail.getFuncBody());
        } else if (FIRSTofNonvoid_specifier.contains(lookahead)) {
            //variable declaration or nonvoid function declaration
            Type type = nonvoid_specifier(union(ID, synch));
            Token token = lookaheadToken;
            match(ID, union(FIRSTofDec_tail, synch));
            DecTail decTail = dec_tail(synch);
            //build ast node
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

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving declaration");
        return null;
    }

    /**
     * 3.	nonvoid-specifier→ int | bool
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Type nonvoid_specifier(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering nonvoid-specifier");

        if (lookahead == INT) {
            match(INT, synch);
            if (traceEnabled) lineTraceOutput.accept("\t\tLeaving nonvoid-specifier");
            return Type.INT;
        }

        match(BOOL, synch);
        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving nonvoid-specifier");
        return Type.BOOL;
    }

    /**
     * 4.	dec-tail → var-dec-tail | fun-dec-tail
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
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

    /**
     * 5.	var-dec-tail → [ [ add-exp ] ]  {|, var-name |}
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private VarDecTail var_dec_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering var-dec-tail");

        AddExpression add_exp = null;
        List<VarName> varNames = new ArrayList<>();

        if (lookahead == LSQR) {
            match(LSQR, union(FIRSTofAdd_expr, synch));
            add_exp = add_exp(union(RSQR, synch));
            match(RSQR, union(synch, SEMI, COMMA));
        }
        while (lookahead == COMMA) {
            match(COMMA, union(FIRSTofVar_name, synch));
            varNames.add(var_name(union(synch, COMMA, SEMI)));
        }
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving var-dec-tail");
        return new VarDecTail(add_exp, varNames);
    }

    /**
     * 6.	var-name → ID [ [ add-exp ] ]
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private VarName var_name(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering var-name");

        Token IDToken = lookaheadToken;
        match(ID, union(LSQR, synch));

        AddExpression add_exp = null;
        if (lookahead == LSQR) {
            match(LSQR, union(FIRSTofAdd_expr, synch));
            add_exp = add_exp(union(RSQR, synch));
            match(RSQR, synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving var-name");
        return new VarName(IDToken, add_exp);
    }

    /**
     * 7.	fun-dec-tail → ( params ) compound-stmt
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private FuncDeclarationTail fun_dec_tail(Set<TokenType> synch) {

        if (traceEnabled) lineTraceOutput.accept("\t\tEntering fun-dec-tail");
        match(LPAREN, union(FIRSTofParams, synch));
        ParamDeclaration params = params(union(RPAREN, synch));
        match(RPAREN, union(FIRSTofCompound_stmt, synch));
        CompoundStatement statement = compound_stmt(synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving fun-dec-tail");

        return new FuncDeclarationTail(params, statement);
    }

    /**
     * 8.	params → param {|, param|} | void
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private ParamDeclaration params(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering params");

        if (lookahead == VOID) {
            match(VOID, synch);
            if (traceEnabled) lineTraceOutput.accept("\t\tLeaving params");
            return ParamDeclaration.voidParam;
        } else {
            ParamDeclaration head = param(union(COMMA, synch));
            ParamDeclaration current = head;
            ParamDeclaration temp;
            while (lookahead == COMMA) {
                match(COMMA, union(FIRSTofParam, synch));
                temp = param(union(COMMA, synch));//unsure about union with comma here
                current.setNextNode(temp);
                current = temp;
            }
            if (traceEnabled) lineTraceOutput.accept("\t\tLeaving params");
            return head;
        }
    }

    /**
     * 9.	param → ref nonvoid-specifier ID | nonvoid-specifier ID [ [ ] ]
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private ParamDeclaration param(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering param");
        boolean isReference = lookahead == REF;
        boolean isArray = false;
        if (isReference) {
            match(REF, union(FIRSTofNonvoid_specifier, synch));
            Type type = nonvoid_specifier(union(ID, synch));
            Token token = lookaheadToken;
            match(ID, synch);
            return new ParamDeclaration(type, token, isArray, isReference);
        } else if (FIRSTofNonvoid_specifier.contains(lookahead)) {
            Type type = nonvoid_specifier(union(ID, synch));
            Token token = lookaheadToken;
            match(ID, union(LSQR, synch));
            if (lookahead == LSQR) {
                match(LSQR, union(RSQR, synch));
                match(RSQR, synch);
                isArray = true;
            }
            return new ParamDeclaration(type, token, isArray, isReference);
        }
        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving param");
        return null;
    }

    /**
     * 10.	statement → id-stmt | compound-stmt | if-stmt | loop-stmt | exit-stmt | continue-stmt | return-stmt | null-stmt | branch-stmt
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
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

    /**
     * 11.	id-stmt → ID id-stmt-tail
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private IdStatement id_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering id-stmt");

        Token idToken = lookaheadToken;
        match(ID, union(FIRSTofId_stmt_tail, synch));

        StatementTail id_stmt_tail = id_stmt_tail(synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving id-stmt");
        return new IdStatement(idToken, id_stmt_tail);
    }

    /**
     * 12.	id-stmt-tail → assign-stmt-tail | call-stmt-tail
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private StatementTail id_stmt_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering id-stmt-tail");

        StatementTail statementTail;
        if (FIRSTofAssign_stmt_tail.contains(lookahead))
            statementTail = assign_stmt_tail(synch);
        else
            statementTail = call_stmt_tail(synch);


        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving id-stmt-tail");
        return statementTail;
    }

    /**
     * 13.	assign-stmt-tail → [ [ add-exp ] ] := expression ;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private StatementTail assign_stmt_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering assign-stmt-tail");

        AddExpression addExpression = null;

        if (lookahead == LSQR) {
            match(LSQR, union(FIRSTofAdd_expr, synch));
            addExpression = add_exp(union(RSQR, synch));
            match(RSQR, union(ASSIGN, synch));
        }

        match(ASSIGN, union(FIRSTofExpression, synch));
        Expression exp = expression(union(SEMI, synch));
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving assign-stmt-tail");
        return new AssignStatementTail(addExpression, exp);
    }

    /**
     * 14.	call-stmt-tail → call-tail ;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private CallStatementTail call_stmt_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering call-stmt-tail");

        Expression call_tail = call_tail(union(SEMI, synch));
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving call-stmt-tail");
        return new CallStatementTail(call_tail);
    }

    /**
     * 15.	call-tail → ( [ arguments ] )
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Expression call_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering call-tail");

        Expression args = null;
        match(LPAREN, union(union(FIRSTofArguments, synch), RPAREN));
        if (FIRSTofArguments.contains(lookahead))
            args = arguments(union(RPAREN, synch));
        match(RPAREN, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving call-tail");
        return args;
    }

    /**
     * 16.	arguments → expression {|, expression|}
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Expression arguments(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering argument");

        Expression exp = expression(union(synch, COMMA));

        Expression tmp = exp;
        while (lookahead == COMMA) {
            match(COMMA, union(synch, FIRSTofExpression));
            Expression tmp2 = expression(union(synch, COMMA));
            tmp.setNextNode(tmp2);
            tmp = tmp2;
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving argument");
        return exp;
    }

    /**
     * 17.	compound-stmt →{ {|nonvoid-specifier ID var-dec-tail |}  {|statment|}+ }
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private CompoundStatement compound_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering compound-stmt");

        match(LCRLY, union(union(synch, FIRSTofNonvoid_specifier), FIRSTofStatement));

        Declaration firstDec = null;
        Declaration tempDec = null;
        while (FIRSTofNonvoid_specifier.contains(lookahead)) {

            Type decType = nonvoid_specifier(union(synch, ID));
            Token IDToken = lookaheadToken;
            match(ID, union(synch, FIRSTofVar_dec_tail));

            var_dec_tail(union(union(synch, FIRSTofStatement), FIRSTofNonvoid_specifier));

            Declaration tempDec2 = new Declaration(decType, IDToken);
            if (firstDec == null) {
                firstDec = tempDec2;
                tempDec = tempDec2;
            } else {
                tempDec.setNextNode(tempDec2);
                tempDec = tempDec2;
            }
        }

        Statement first = null;
        Statement tempStmt = null;
        do {
            Statement temp = statement(union(union(synch, FIRSTofStatement), RCRLY));

            if (first == null) {
                first = temp;
                tempStmt = temp;
            } else {
                tempStmt.setNextNode(temp);
                tempStmt = temp;
            }
        } while (FIRSTofStatement.contains(lookahead));

        match(RCRLY, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving compound-stmt");
        return new CompoundStatement(firstDec, first);
    }

    /**
     * 19.	loop-stmt → loop {| statement |}+¬ end ;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Statement loop_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering loop-stmt");

        match(LOOP, union(synch, FIRSTofStatement));

        Statement first = statement(union(union(synch, FIRSTofStatement), END));
        Statement temp = first;
        while (FIRSTofStatement.contains(lookahead)) {
            Statement temp2 = statement(union(union(synch, FIRSTofStatement), END));
            temp.setNextNode(temp2);
            temp = temp2;
        }
        match(END, union(synch, SEMI));
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving loop-stmt");
        return new LoopStatement(first);
    }

    /**
     * 20.	exit-stmt → exit ;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Statement exit_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering exit-stmt");

        match(EXIT, union(synch, EXIT));
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving exit-stmt");
        return new ExitStatement();
    }

    /**
     * 21.	continue-stmt → continue ;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Statement continue_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering continue-stmt");

        match(CONTINUE, union(synch, SEMI));
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving continue-stmt");
        return new ContinueStatement();
    }

    /**
     * 22.	return-stmt → return [ expression ] ;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private ReturnStatement return_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering return-stmt");

        match(RETURN, union(union(synch, FIRSTofExpression), SEMI));

        Expression returnValue = null;
        if (FIRSTofExpression.contains(lookahead))
            returnValue = expression(union(synch, SEMI));

        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving return-stmt");
        return new ReturnStatement(returnValue);
    }

    /**
     * 23.	null-stmt → ;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Statement null_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering null-stmt");
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving null-stmt");
        return new NullStatement();
    }

    /**
     * 24.	branch-stmt → branch ( add-exp ) {| case |}+ end;
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private BranchStatement branch_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering branch-stmt");

        match(BRANCH, union(synch, LPAREN));
        match(LPAREN, union(synch, FIRSTofAdd_expr));

        AddExpression addexp = add_exp(union(synch, RPAREN));

        match(RPAREN, union(synch, FIRSTofCase_stmt));

        CaseStatement caseStmt = case_stmt(union(synch, FIRSTofCase_stmt, END));
        CaseStatement next = caseStmt;
        while (FIRSTofCase_stmt.contains(lookahead)) {
            CaseStatement temp = case_stmt(union(synch, FIRSTofCase_stmt, END));
            next.setNextNode(temp);
            next = temp;
        }

        match(END, union(synch, SEMI));
        match(SEMI, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving branch-stmt");
        return new BranchStatement(addexp, caseStmt);
    }

    /**
     * 25.	case → case NUM : statement | default : statement
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private CaseStatement case_stmt(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering case-stmt");

        Statement statement;
        Token t = null;
        if (lookahead == CASE) {
            match(CASE, union(synch, NUM));
            t = lookaheadToken;
            match(NUM, union(synch, COLON));
            match(COLON, union(synch, FIRSTofStatement));
            statement = statement(synch);
        } else {
            match(DEFAULT, union(synch, COLON));
            match(COLON, union(synch, FIRSTofStatement));
            statement = statement(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving case-stmt");
        return new CaseStatement(t, statement);
    }

    /**
     * 26.	expression → add-exp [ relop add-exp ]
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Expression expression(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering expression");

        AddExpression addExpression = add_exp(union(synch, FIRSTofRelop));

        if (FIRSTofRelop.contains(lookahead)) {

            TokenType relop = relop(union(synch, FIRSTofAdd_expr));
            AddExpression addExp2 = add_exp(synch);

            return new Expression(addExpression, relop, addExp2);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving expression");
        return new Expression(addExpression, null, null);
    }

    /**
     * 27.	add-exp → [ uminus ] term {| addop term |}
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private AddExpression add_exp(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering add-exp");

        boolean minusExpr = false;
        if (FIRSTofUMinus.contains(lookahead)) {
            uminus(union(synch, FIRSTofTerm));
            minusExpr = true;
        }

        Term term = term(union(synch, FIRSTofAddOp));

        AddExpression result = new AddExpression(minusExpr, term);
        TokenType tempAddOp;
        Term tempTerm;
        ASTNode current = result;
        AddOpTerm next;

        while (FIRSTofAddOp.contains(lookahead)) {
            tempAddOp = addop(union(synch, FIRSTofTerm));
            tempTerm = term(union(synch, FIRSTofAddOp));
            next = new AddOpTerm(tempAddOp, tempTerm);
            current.setNextNode(next);
            current = next;
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving add-exp");
        return result;
    }

    /**
     * 28.	term → factor {| multop factor |}
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Term term(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering term");

        SubExpression factor = factor(union(synch, FIRSTofMultop));
        Term result = new Term(factor);
        TokenType tempMultOp = null;
        SubExpression tempFactor = null;
        SubExpression current = result;
        MultOpFactor next;

        while (FIRSTofMultop.contains(lookahead)) {
            tempMultOp = multop(union(synch, FIRSTofFactor));
            tempFactor = factor(union(synch, FIRSTofMultop));
            next = new MultOpFactor(tempMultOp, tempFactor);
            current.setNextNode(next);
            current = next;
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving term");
        return result;
    }

    /**
     * 29.	factor → nid-factor | id-factor
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private SubExpression factor(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering factor");

        SubExpression factor;
        if (FIRSTofNid_factor.contains(lookahead)) {
            factor = nid_factor(synch);
        } else {
            factor = id_factor(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving factor");
        return factor;
    }

    /**
     * 30.	nid-factor → not factor | ( expression ) | NUM | BLIT
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private SubExpression nid_factor(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering nid-factor");

        SubExpression nidFactor = null;
        switch (lookahead) {
            case NOT:
                match(NOT, union(synch, FIRSTofFactor));
                nidFactor = new NotNidFactor(factor(synch));
                break;
            case LPAREN:
                match(LPAREN, union(synch, FIRSTofExpression));
                nidFactor = expression(union(synch, RPAREN));
                match(RPAREN, synch);
                break;
            case NUM:
                Token numToken = lookaheadToken;
                match(NUM, synch);
                nidFactor = new LiteralNum((Integer) numToken.attrValue);
                break;
            case BLIT:
                Token blitToken = lookaheadToken;
                match(BLIT, synch);
                nidFactor = new LiteralBool((Integer) blitToken.attrValue);
                break;
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving nid-factor");
        return nidFactor;
    }

    /**
     * 31.	id-factor → ID id-tail
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private Factor id_factor(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering id-factor");

        Token IDToken = lookaheadToken;
        match(ID, union(synch, FIRSTofId_tail));
        ASTNode idTail = id_tail(synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving id-factor");
        return new IdFactor(IDToken, idTail);
    }

    /**
     * 32.	id-tail → var-tail | call-tail
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private ASTNode id_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering id-tail");

        ASTNode idTail;
        if (FIRSTofCall_tail.contains(lookahead)) {
            idTail = call_tail(synch);
        } else {
            idTail = var_tail(synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving id-tail");
        return idTail;
    }

    /**
     * 33.	var-tail → [ [ add-exp ] ]
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private ASTNode var_tail(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering var-tail");

        AddExpression add_exp = null;
        if (lookahead == LSQR) {
            match(LSQR, union(synch, FIRSTofAdd_expr));
            add_exp = add_exp(union(synch, RSQR));
            match(RSQR, synch);
        }

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving var-tail");
        return add_exp;
    }

    /**
     * 34.	relop → <= | < | > | >= | = | /=
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
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

    /**
     * 35.	addop → + | - | or | ||
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
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

    /**
     * 36.	multop → * | / | mod | and | &&
     *
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
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

    /**
     * 37.	uminus → -
     * 
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     * @return
     */
    private MinusExpression uminus(Set<TokenType> synch) {
        if (traceEnabled) lineTraceOutput.accept("\t\tEntering unimus");

        match(MINUS, synch);

        if (traceEnabled) lineTraceOutput.accept("\t\tLeaving unimus");
        return new MinusExpression();
    }

    /**
     * Drives the scanner to produce more tokens when parsing, sets up the lookahead token, and verifies that tokens expected by the production rule methods are found.
     * 
     * @param expected - The token expected by the calling production rule method
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     */
    private void match(TokenType expected, Set<TokenType> synch) {
        if (lookahead == expected) {
            if (traceEnabled) lineTraceOutput.accept("\t\tMatched " + expected.toString());

            lookaheadToken = scanner.nextToken();

            this.tokens.add(lookaheadToken);
            lookahead = lookaheadToken.token;
        } else
            syntaxError(expected, synch);
        syntaxCheck(synch);
    }

    /**
     * Checks if a syntax error has been encountered using the synch set. If an error is encountered, an error message is produced and error recovery begins.
     * 
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     */
    private void syntaxCheck(Set<TokenType> synch) {
        if (!synch.contains(lookahead)) {
            errorOutput.accept("\t\tSyntax Error, token:" + lookahead + " is not expected here.");
            syntaxError(synch);
        }
    }

    /**
     * Called when an expected token does not match the lookahead token. If an error is encountered, an error message is produced and error recovery begins.
     *
     * @param expected
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     */
    private void syntaxError(TokenType expected, Set<TokenType> synch) {
        errorOutput.accept("\t\tSyntax Error, expecting " + expected + " but received " + lookahead);
        syntaxError(synch);
    }

    /**
     * Implementation of panic mode error recovery. Consumes tokens until a token that is contained in the synch set is encountered.
     * 
     * @param synch - Set of tokens collected during parsing which represents valid next tokens, used for error recovery purposes
     */
    private void syntaxError(Set<TokenType> synch) {
        syntaxError = true;
        while (!synch.contains(lookahead)) {
            errorOutput.accept("\t\tConsuming " + lookahead + " because it is not expected.");
            lookaheadToken = scanner.nextToken();
            this.tokens.add(lookaheadToken);
            lookahead = lookaheadToken.token;
        }
    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    private Set<TokenType> union(Set<TokenType> synch, Set<TokenType> firstSet) {
        Set<TokenType> newSynch = new HashSet<>(synch);
        newSynch.addAll(firstSet);
        return newSynch;
    }

    private Set<TokenType> union(TokenType token, Set<TokenType> synch) {
        Set<TokenType> newSynch = new HashSet<>(synch);
        newSynch.add(token);
        return newSynch;
    }

    private Set<TokenType> union(Set<TokenType> synch, Set<TokenType> firsTofCase_stmt, TokenType... tts) {
        Set<TokenType> newSynch = new HashSet<>(synch);
        newSynch.addAll(firsTofCase_stmt);
        newSynch.addAll(Arrays.asList(tts));
        return newSynch;
    }

    private Set<TokenType> union(Set<TokenType> synch, TokenType... tt) {
        Set<TokenType> newSynch = new HashSet<>(synch);
        newSynch.addAll(Arrays.asList(tt));
        return newSynch;
    }

}
