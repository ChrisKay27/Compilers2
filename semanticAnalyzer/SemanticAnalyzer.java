package semanticAnalyzer;

import parser.Type;
import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.ParamDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.*;
import parser.grammar.statements.*;
import scanner.Token;
import scanner.TokenType;

import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static parser.Type.*;

/**
 * Created by Carston on 2/27/2016.
 */
public class SemanticAnalyzer implements SemAnalInter {

    private final Consumer<String> output;
    private final Consumer<String> singleError;
    private ASTNode astRoot;
    private SymbolTable symbolTable;
    private BiConsumer<Integer, String> lineError;
    private boolean foundError;
    private boolean traceEnabled;

    private Stack<Integer> levelStack = new Stack<>();
    private Stack<Integer> localVariableCountStack = new Stack<>();

    private FuncDeclaration currentFuncDecl;
    private static Declaration errorDeclaration = new Declaration(-1, Type.UNIV, null);
    private boolean returnStatementFound;

    //Keeps track of any loops that we are currently in (as we look through the tree) to see if an exit or continue is not
    //inside of a loop
    private Stack<LoopStatement> currentLoop = new Stack<>();


    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public SemanticAnalyzer(ASTNode astRoot, Consumer<String> output, Consumer<String> singleError, BiConsumer<Integer, String> lineError) {
        this.astRoot = astRoot;
        this.output = output;
        this.singleError = singleError;
        this.lineError = lineError;

        this.symbolTable = new SymbolTable();
    }


    public boolean startSemAnal(Declaration AST) {


        //Library functions
        Token readIntToken = new Token(TokenType.ID, 0);
        readIntToken.name = "readint";
        Token writeIntToken = new Token(TokenType.ID, 1);
        writeIntToken.name = "writeint";
        Token readBoolToken = new Token(TokenType.ID, 2);
        readBoolToken.name = "readbool";
        Token writeBoolToken = new Token(TokenType.ID, 3);
        writeBoolToken.name = "writebool";
        addDeclaration(new FuncDeclaration(0, INT, readIntToken, null, null));
        addDeclaration(new FuncDeclaration(0, Type.VOID, writeIntToken, new ParamDeclaration(0, INT, new Token(TokenType.ID, 4), false, false), null));
        addDeclaration(new FuncDeclaration(0, Type.BOOL, readBoolToken, null, null));
        addDeclaration(new FuncDeclaration(0, Type.VOID, writeBoolToken, new ParamDeclaration(0, BOOL, new Token(TokenType.ID, 4), false, false), null));

        Declaration current = AST;
        FuncDeclaration lastFunction = null;
        do {
            current.setLevel(0);
            System.out.println("Adding Top Level Decl");
            addDeclaration(current);
            current = current.getNextNode();
        }
        while (current != null);

        current = AST;
        do {
            if (current instanceof VarDeclaration) {
                analyze((VarDeclaration) current);
            } else {

                analyze((FuncDeclaration) current);
                lastFunction = (FuncDeclaration) current;
            }

            current = (Declaration) current.getNextNode();
        }
        while (current != null);

        mainFunctionConstraints(lastFunction);


        return !foundError;
    }

    public void analyze(FuncDeclaration AST) {
        output.accept(AST.getLine() + ": analyze FuncDeclaration\n");

        enteringFuncDeclaration(AST);

        analyze(AST.getParams());
        AST.setNumberOfParameters(AST.getParams().getLength());

        analyze(AST.getBody(), true);
        AST.setNumberOfLocals(AST.getBody().getDeclarations().getLength());

        functionConstraints(AST);

        leavingFuncDeclaration();
    }

    private void functionConstraints(FuncDeclaration node) {
        if ((!returnStatementFound) && node.getType() != Type.VOID) {
            foundError = true;
            lineError.accept(currentFuncDecl.getLine(), "No return statement found.");
        }
    }

    private void mainFunctionConstraints(FuncDeclaration function) {
        if (function == null) {
            foundError = true;
            lineError.accept(0, "No main function in file");
        } else if ("main".equals(function.getID().name)) {
            if (function.getType() != Type.INT) {
                foundError = true;
                lineError.accept(function.getLine(), "Function main must have a return type of int.");
            }
            if (function.getParams().getType() != Type.VOID && !(function.getParams().getNextNode() instanceof ParamDeclaration)) {
                foundError = true;
                lineError.accept(function.getLine(), "Function main must have no parameters.");
            }
        } else {
            foundError = true;
            lineError.accept(function.getLine(), "Function main must be the last function declared in the source file.");
        }
    }

    private void enteringFuncDeclaration(FuncDeclaration AST) {
        output.accept(AST.getLine() + ": enteringFuncDeclaration\n");
        enteringCompoundStmt();
        if (currentFuncDecl != null) {
            foundError = true;
            lineError.accept(AST.getLine(), "No functions are allowed to be inside other functions");
        }
        currentFuncDecl = AST;
        this.returnStatementFound = false;

        //update what level we are on
        levelStack.push(1);
        localVariableCountStack.push(0);
    }

    private void leavingFuncDeclaration() {
        output.accept("  leavingFuncDeclaration\n");
        currentFuncDecl = null;
        leavingCompoundStmt();

        levelStack.pop();
        localVariableCountStack.pop();
    }

    public void analyze(ParamDeclaration AST) {
        output.accept(AST.getLine() + ": analyze ParamDeclaration\n");
        int length = -1;
        while (AST != null) {
            if (AST.getType() != VOID) {
                System.out.println("Adding Param Decl");
                addDeclaration(AST);

                AST.setLevel(1);
                //Parameters are indexed with negative displacements starting at -1 for the first
                AST.setDisplacement(length--);
            }
            AST = (ParamDeclaration) AST.getNextNode();
        }
    }

    public void analyze(VarDeclaration AST) {
        output.accept(AST.getLine() + ": analyze VarDeclaration\n");

        if (AST.getLevel() != 0) {
//            while (AST != null) {
                AST.setLevel(levelStack.peek());
                AST.setDisplacement(2 + localVariableCountStack.peek());
                localVariableCountStack.push(localVariableCountStack.pop() + 1);
                System.out.println("Adding Decl");
                addDeclaration(AST);
                AST = (VarDeclaration) AST.getNextNode();
//            }
        }
    }

    public void analyze(Statement AST) {
        output.accept(AST.getLine() + ": analyze Statement\n");
        if (AST instanceof IdStatement)
            analyze((IdStatement) AST);
        if (AST instanceof IfStatement)
            analyze((IfStatement) AST);
        if (AST instanceof CompoundStatement)
            analyze((CompoundStatement) AST, false);
        if (AST instanceof LoopStatement)
            analyze((LoopStatement) AST);
        if (AST instanceof ContinueStatement)
            analyze((ContinueStatement) AST);
        if (AST instanceof ExitStatement)
            analyze((ExitStatement) AST);
        if (AST instanceof ReturnStatement)
            analyze((ReturnStatement) AST);
        if (AST instanceof BranchStatement)
            analyze((BranchStatement) AST);
        if (AST instanceof CaseStatement)
            analyze((CaseStatement) AST);
        else if (AST instanceof NullStatement)
            analyze((NullStatement) AST);
    }

    public void analyze(IdStatement AST) {
        output.accept(AST.getLine() + ": analyze IdStatement\n");
        AST.setDecl(getDeclaration(AST.getLine(), AST.getIdToken()));

        analyze(AST.getId_stmt_tail());

        Declaration d = AST.getDecl();
        if (d instanceof FuncDeclaration) {
            if ((AST.getId_stmt_tail() instanceof AssignStatementTail)) {
                foundError = true;
                lineError.accept(AST.getLine(), d.getID() + " is a function declaration and it is being assigned to.");
            } else
                callConstraints(AST);
        }


        if (d instanceof VarDeclaration) {
            VarDeclaration varDec = (VarDeclaration) d;

            if (AST.getId_stmt_tail() instanceof CallStatementTail) {
                foundError = true;
                lineError.accept(AST.getLine(), d.getID() + " is a var declaration and its being called as a function.");
            } else {
                AssignStatementTail assignTail = (AssignStatementTail) AST.getId_stmt_tail();

                //If we are trying to index this variable and it is not an array...
                if (assignTail.getAddExpression() != null && !varDec.isAnArray()) {
                    foundError = true;
                    lineError.accept(AST.getLine(), d.getID() + " is not an array and we are trying to index it.");
                } else if (varDec.isAnArray()) {
                    if (((AssignStatementTail) AST.getId_stmt_tail()).getAddExpression() == null) {
                        foundError = true;
                        String temp;
                        switch (d.getType()) {
                            case INT:
                                temp = "an";
                                break;
                            case BOOL:
                                temp = "a";
                                break;
                            default:
                                temp = "an";
                                break;
                        }
                        lineError.accept(AST.getLine(), d.getID() + " is declared as an array but is being used as " + temp + " " + (d.getType().toString()).toLowerCase());
                    } else {
                        int indexExpression = ((AssignStatementTail) AST.getId_stmt_tail()).getAddExpression().evaluateStaticInt();
                        System.out.println("INDEX: " + indexExpression);
                        if (indexExpression >= ((VarDeclaration) AST.getDecl()).getArraySize() || indexExpression < 0) {
                            foundError = true;
                            lineError.accept(AST.getLine(), "Array index out of bounds, " + indexExpression + " not within [0, " + ((VarDeclaration) AST.getDecl()).getArraySize() + ")");
                        }
                    }
                }
            }
        }
    }

    private void callConstraints(IdStatement idStatement) {
        FuncDeclaration function = (FuncDeclaration) idStatement.getDecl();
        Expression args = (Expression) ((CallStatementTail) idStatement.getId_stmt_tail()).getCall_tail();

        ((CallStatementTail) idStatement.getId_stmt_tail()).setFuncDecl(function);

        ParamDeclaration currentParam = function.getParams();
        Expression currentArg = args;
        int counter = 0;
        while (currentArg != null && currentParam != null) {
            counter++;
            if (currentArg.getType() != currentParam.getType()) {
                if (currentArg.getType() != Type.UNIV) {
                    foundError = true;
                    lineError.accept(idStatement.getLine(), idStatement.getIdToken().name +
                            ", Argument #" + counter + " : Expected argument of type " + currentParam.getType()
                            + ", received argument of type " + currentArg.getType());
                } else {

                }
            }

            if (currentParam.getNextNode() instanceof ParamDeclaration) {
                currentParam = (ParamDeclaration) currentParam.getNextNode();
            } else {
                currentParam = null;
            }

            if (currentArg.getNextNode() instanceof Expression) {
                currentArg = (Expression) currentArg.getNextNode();
            } else {
                currentArg = null;
            }
        }

        if (currentArg == null && currentParam != null && currentParam != ParamDeclaration.voidParam) {

            foundError = true;
            lineError.accept(idStatement.getLine(), "Function call " + idStatement.getIdToken().name
                    + " has not enough arguments provided.");
        } else if (currentParam == null && currentArg != null) {
            foundError = true;
            lineError.accept(idStatement.getLine(), "Function call " + idStatement.getIdToken().name
                    + " has too many arguments provided.");
        }
    }

    public void analyze(StatementTail AST) {
        output.accept(AST.getLine() + ": analyze StatementTail\n");
        if (AST instanceof AssignStatementTail)
            analyze((AssignStatementTail) AST);
        else if (AST instanceof CallStatementTail)
            analyze((CallStatementTail) AST);
        else if (AST == null) {
            System.out.println("AST = [" + AST + "]");
        }
    }

    public void analyze(AssignStatementTail AST) {
        output.accept(AST.getLine() + ": analyze AssignStatementTail\n");
        if (AST.getAddExpression() != null) {
            analyze(AST.getAddExpression());

        }
        analyze(AST.getExp());
    }

    public void analyze(IfStatement AST) {
        output.accept(AST.getLine() + ": analyze IfStatement\n");
        analyze(AST.getExpression());
        analyze(AST.getStatement());
        Statement elseStatement = AST.getElseStatement();
        if (elseStatement != null) analyze(elseStatement);
    }

    public void analyze(CallStatementTail AST) {
        output.accept(AST.getLine() + ": analyze CallStatementTail\n");
        if (AST.getCall_tail() != null) {
            Expression expression = (Expression) AST.getCall_tail();
            Expression current = expression;
            while (current != null) {

                analyze(current);

                if (current.getNextNode() instanceof Expression) {
                    current = (Expression) current.getNextNode();
                } else {
                    current = null;
                }
            }
        }
    }

    public void analyze(CompoundStatement AST, boolean funcBody) {
        if( funcBody ) {
            output.accept(AST.getLine() + ": analyze FunctionBody\n");
        }
        else {
            output.accept(AST.getLine() + ": analyze CompoundStatement\n");
            enteringCompoundStmt();
        }

        Declaration d = AST.getDeclarations();
        while (d != null) {
            //addDeclaration(d);
            analyze((VarDeclaration) d);
            d = d.getNextNode();
        }

        Statement stmt = AST.getStatements();

        while (stmt != null) {
            analyze(stmt);
            stmt = (Statement) stmt.getNextNode();
        }
        if( !funcBody )
            leavingCompoundStmt();
    }

    public void analyze(LoopStatement AST) {
        output.accept(AST.getLine() + ": analyze LoopStatement\n");
        currentLoop.push(AST);

        Statement statement = AST.getStatement();
        loopConstraints(statement);
        analyze(statement);

        currentLoop.pop();
    }

    private void loopConstraints(Statement statement) {
        while (statement != null) {
            statement = (Statement) statement.getNextNode();
            if (statement instanceof ExitStatement) {

            } else if (statement instanceof ContinueStatement) {

            } else if (statement instanceof ReturnStatement) {

            } else if (statement instanceof LoopStatement) {
                LoopStatement innerLoop = (LoopStatement) statement;
                innerLoopConstraints(innerLoop.getStatement());
            }
        }
    }

    private void innerLoopConstraints(Statement statement) {
        while (statement != null) {
            statement = (Statement) statement.getNextNode();
            if (statement instanceof ReturnStatement) {

            } else if (statement instanceof LoopStatement) {
                LoopStatement innerLoop = (LoopStatement) statement;
                innerLoopConstraints(innerLoop.getStatement());
            }
        }
    }

    public void analyze(ExitStatement AST) {
        output.accept(AST.getLine() + ": analyze ExitStatement\n");
        if (!isInsideLoop()) {
            lineError.accept(AST.getLine(), "Exit statement not within loop");
            foundError = true;
        }
    }

    private boolean isInsideLoop() {
        return !currentLoop.empty();
    }

    public void analyze(ContinueStatement AST) {
        output.accept(AST.getLine() + ": analyze ContinueStatement\n");
        if (!isInsideLoop()) {
            lineError.accept(AST.getLine(), "Continue statement not within loop");
            foundError = true;
        }
    }

    public void analyze(ReturnStatement AST) {
        output.accept(AST.getLine() + ": analyze ReturnStatement\n");
        analyze(AST.getReturnValue());

        if (currentFuncDecl == null) {
            foundError = true;
            lineError.accept(AST.getLine(), "Error, return statement not within function.");
        }

        if (AST.getReturnValue().getType() != currentFuncDecl.getType()) {
            foundError = true;
            lineError.accept(AST.getLine(), "Error, return type does not match functions type.");
        } else {
            returnStatementFound = true;
            AST.setFuncDecl(currentFuncDecl);
        }
    }

    public void analyze(NullStatement AST) {
        output.accept(AST.getLine() + ": analyze NullStatement\n");
    }

    public void analyze(BranchStatement AST) {
        output.accept(AST.getLine() + ": analyze BranchStatement\n");
        branchConstraints(AST.getCaseStmt());
        analyze(AST.getAddexp());
        analyze(AST.getCaseStmt());
    }

    /**
     * Ensures that a branch statement contains at most one default case
     *
     * @param statement
     */
    private void branchConstraints(CaseStatement statement) {
        CaseStatement stmt = statement;
        boolean defaultCaseFound = false;
        while (statement.getNextNode() != null) {
            if (stmt.getNumberToken() == null) {
                if (defaultCaseFound) {
                    // semantic lineError
                }
                defaultCaseFound = true;
            }
            stmt = (CaseStatement) stmt.getNextNode();
        }
    }

    public void analyze(CaseStatement statement) {
        analyze(statement.getStatement()); // statement for this case
        analyze(statement.getNextNode()); // next case in branch statement
    }

    public Type analyze(Expression AST) {
        output.accept(AST.getLine() + ": analyze Expression\n");

        Type t = analyze(AST.getAddExp());
        AST.setType(t);

        boolean isStatic = AST.getAddExp().isStatic();

        if (AST.getAddExp2() != null) {
            Type t2 = analyze(AST.getAddExp2());

            AST.setType(typeCheck(t, t2, AST.getRelop().getOperandTypes()));

            isStatic &= AST.getAddExp2().isStatic();
//
//            if (t == UNIV || t2 == UNIV) {
//                AST.setType(UNIV);
//            } else {
//                final List<Type> operandTypes = AST.getRelop().getOperandTypes();
//                if (t != t2 || (!operandTypes.contains(t) || !operandTypes.contains(t2))) {
//                    foundError = true;
//                    lineError.accept(AST.getLine(),"Expression type mismatch");
//                    AST.setType(UNIV);
//                } else
//                    AST.setType(t);
//            }
        }

        AST.setStatic(isStatic);

        return AST.getType();
    }

    public Type analyze(AddExpression AST) {
        output.accept(AST.getLine() + ": analyze AddExpression\n");

        Type t = analyze(AST.getTerm());
        AST.setType(t);

        boolean isStatic = AST.getTerm().isStatic();

        if (t == BOOL && AST.isUminus()) {
            t = UNIV;
            foundError = true;
            lineError.accept(AST.getLine(), "Cannot urinary minus a boolean!");
        }

        AddOpTerm next = AST.getNextNode();
        if (next != null) {
            Type t2 = analyze(next);

            AST.setType(typeCheck(t, t2, next.getAddOp().getOperandTypes()));

            isStatic &= next.isStatic();
        }

        AST.setStatic(isStatic);


        return t;
    }

    public Type analyze(Term AST) {
        output.accept(AST.getLine() + ": analyze Term\n");

        Type t = analyze(AST.getFactor());
        AST.setType(t);

        boolean isStatic = AST.getFactor().isStatic();

        MultOpFactor next = AST.getNextNode();
        if (next != null) {
            Type t2 = analyze(next);

            AST.setType(typeCheck(t, t2, next.getMultOp().getOperandTypes()));

            isStatic &= next.isStatic();
        }

        AST.setStatic(isStatic);

        return t;
    }

    public Type analyze(SubExpression AST) {
        output.accept(AST.getLine() + ": analyze SubExpression\n");

        if (AST instanceof IdFactor) {
            IdFactor id = (IdFactor) AST;
            return analyze(id);
        } else if (AST instanceof LiteralBool)
            return analyze((LiteralBool) AST);

        else if (AST instanceof LiteralNum)
            return analyze((LiteralNum) AST);

        else if (AST instanceof AddOpTerm)
            return analyze(((AddOpTerm) AST).getTerm());

        else if (AST instanceof MultOpFactor)
            return analyze((MultOpFactor) AST);

        else if (AST instanceof NotNidFactor)
            return analyze((NotNidFactor) AST);

        else if (AST instanceof Term)
            return analyze((Term) AST);

        else if (AST instanceof AddExpression)
            return analyze((AddExpression) AST);

        else if (AST instanceof Expression)
            return analyze((Expression) AST);


        return UNIV;
    }

    public Type analyze(LiteralBool AST) {
        output.accept(AST.getLine() + ": analyze LiteralBool\n"); //Nothing to analyze here
        return BOOL;
    }

    public Type analyze(LiteralNum AST) {
        output.accept(AST.getLine() + ": analyze LiteralNum\n"); //Nothing to analyze here
        return INT;
    }

    public Type analyze(MultOpFactor AST) {
        output.accept(AST.getLine() + ": analyze MultOpFactor\n");
        return analyze(AST.getFactor());
    }

    public Type analyze(NotNidFactor AST) {
        output.accept(AST.getLine() + ": analyze NotNidFactor\n");

        Type t = analyze(AST.getFactor());
        if (t != BOOL)
            return UNIV;
        return BOOL;
    }

    public Type analyze(IdFactor AST) {
        output.accept(AST.getLine() + ": analyze IdFactor\n");

        AST.setStatic(false);

        AST.setDecl(getDeclaration(AST.getLine(), AST.getIdToken()));

        if (AST.getIdTail() instanceof CallStatementTail)
            analyze((CallStatementTail) AST.getIdTail());

        else if (AST.getIdTail() instanceof AddExpression) {
            analyze(AST.getIdTail());

            if (!((AddExpression) AST.getIdTail()).isStatic()) {
                foundError = true;
                lineError.accept(AST.getLine(), "Non static expression in array index");
            }

            int indexExpression = ((AddExpression) AST.getIdTail()).evaluateStaticInt();
            System.out.println("INDEX: " + indexExpression);
            if (indexExpression >= ((VarDeclaration) AST.getDecl()).getArraySize() || indexExpression < 0) {
                foundError = true;
                lineError.accept(AST.getLine(), "Array index out of bounds, " + indexExpression + " not within [0, " + ((VarDeclaration) AST.getDecl()).getArraySize() + ")");
            }
        }

        return AST.getDecl().getType();
    }

    private void enteringCompoundStmt() {
        output.accept("  enteringCompoundStmt\n");
        symbolTable.enterFrame();
    }

    private void leavingCompoundStmt() {
        output.accept("  leavingCompoundStmt\n");
        symbolTable.leaveFrame();
    }

    private Declaration getDeclaration(int line, Token id) {
        SymbolTableEntry d = symbolTable.get(id.getID());

        if (d == null) {
            foundError = true;
            lineError.accept(line, "No declaration for this id: " + id);
            return errorDeclaration;
        }

        return (Declaration) d.getNode();
    }

    public void addDeclaration(Declaration d) {

        boolean duplicateDefiniton = symbolTable.push(new SymbolTableEntry(d.getID().getID(), d));
        if (!duplicateDefiniton)
            lineError.accept(d.getLine(), "Duplicate definition of " + d.getID().name);
    }

    @Override
    public void analyze(ASTNode AST) {
        output.accept(AST.getLine() + ": analyze ASTNode\n");
    }


    public Type typeCheck(Type t, Type t2, List<Type> operandTypes) {
        //If any of them are already UNIV then we don't report the lineError because it has already been reported.
        if (t == UNIV || t2 == UNIV)
            return UNIV;

        //If the types do not match or the operand types available do not contain one of these operators then report lineError
        if (t != t2 || (!operandTypes.contains(t) || !operandTypes.contains(t2))) {
            foundError = true;
            singleError.accept("Expression type mismatch");
            return UNIV;
        }
        //Everything is fine, return the type
        return t;
    }

}
