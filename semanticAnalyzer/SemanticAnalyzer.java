package semanticAnalyzer;

import parser.TokenType;
import parser.Type;
import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.ParamDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.*;
import parser.grammar.statements.*;
import scanner.Token;
import util.WTFException;

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
    private final Consumer<String> regError;
    private ASTNode astRoot;
    private SymbolTable symbolTable;
    private BiConsumer<Integer, String> error;
    private boolean foundError;
    private boolean traceEnabled;

    private FuncDeclaration currentFuncDecl;
    private static Declaration errorDeclaration = new Declaration(-1, Type.ERROR, null);

    //Keeps track of any loops that we are currently in (as we look through the tree) to see if an exit or continue is not
    //inside of a loop
    private Stack<LoopStatement> currentLoop = new Stack<>();

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public SemanticAnalyzer(ASTNode astRoot, Consumer<String> output, Consumer<String> error, BiConsumer<Integer, String> lineError) {
        this.astRoot = astRoot;
        this.output = output;
        this.regError = error;
        this.error = lineError;

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
            addDeclaration(current);
            current = (Declaration) current.getNextNode();
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

        analyze(AST.getBody());
        functionConstraints(AST);

        leavingFuncDeclaration();
    }

    private void functionConstraints(FuncDeclaration node) {
        Statement statement = node.getBody();
        Type type = node.getType();
        ReturnStatement returnStatement = findReturn(statement);
        if ((returnStatement == null) && type != Type.VOID) {
            foundError = true;
            error.accept(currentFuncDecl.getLine(), "No return statement found.");
        }

        if (returnStatement != null)
            if (returnStatement.getType() != type) {
                foundError = true;
                error.accept(statement.getLine(), "The returned value must match the return type of the function.");
            }

    }

    private ReturnStatement findReturn(Statement statement) {
        ReturnStatement returnStatement = null;
        while (statement != null) {
            if (statement instanceof CompoundStatement) {
                returnStatement = findReturn(((CompoundStatement) statement).getStatements());
            }
            statement = (Statement) statement.getNextNode();
            if (statement instanceof ReturnStatement) {
                returnStatement = (ReturnStatement) statement;
            }
        }
        return returnStatement;
    }

    private void mainFunctionConstraints(FuncDeclaration function) {
        if (function != null && "main".equals(function.getID().name)) {
            if (function.getType() != Type.INT) {
                error.accept(function.getLine(), "Function main must have a return type of int.");
            }
            if (function.getParams().getType() != Type.VOID && !(function.getParams().getNextNode() instanceof ParamDeclaration)) {
                error.accept(function.getLine(), "Function main must have no parameters.");
            }
        } else {
            error.accept(function.getLine(), "Function main must be the last function declared in the source file.");
        }
    }

    private void enteringFuncDeclaration(FuncDeclaration AST) {
        output.accept(AST.getLine() + ": enteringFuncDeclaration\n");
        enteringCompoundStmt();
        if (currentFuncDecl != null)
            throw new WTFException("No functions are allowed to be inside other functions");
        currentFuncDecl = AST;
    }

    private void leavingFuncDeclaration() {
        output.accept("  leavingFuncDeclaration\n");
        currentFuncDecl = null;
        leavingCompoundStmt();
    }

    public void analyze(ParamDeclaration AST) {
        output.accept(AST.getLine() + ": analyze ParamDeclaration\n");
        while (AST != null) {
            if (AST.getType() != VOID)
                addDeclaration(AST);
            AST = (ParamDeclaration) AST.getNextNode();
        }
    }

    public void analyze(VarDeclaration AST) {
        output.accept(AST.getLine() + ": analyze VarDeclaration\n");
        //Dec already added in first sweep phase
    }

    public void analyze(Statement AST) {
        output.accept(AST.getLine() + ": analyze Statement\n");
        if (AST instanceof IdStatement)
            analyze((IdStatement) AST);
        if (AST instanceof IfStatement)
            analyze((IfStatement) AST);
        if (AST instanceof CompoundStatement)
            analyze((CompoundStatement) AST);
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
        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));
        analyze(AST.getId_stmt_tail());

        Declaration d = AST.getDecl();
        if (d instanceof FuncDeclaration) {
            if ((AST.getId_stmt_tail() instanceof AssignStatementTail))
                error.accept(AST.getLine(), d.getID() + " is a function declaration and it is being assigned to.");
            else
                callConstraints(AST);
        }


        if (d instanceof VarDeclaration) {
            VarDeclaration varDec = (VarDeclaration) d;

            if (AST.getId_stmt_tail() instanceof CallStatementTail)
                error.accept(AST.getLine(), d.getID() + " is a var declaration and its being called as a function.");
            else {
                AssignStatementTail assignTail = (AssignStatementTail) AST.getId_stmt_tail();

                //If we are trying to index this variable and it is not an array...
                if (assignTail.getAddExpression() != null && !varDec.isAnArray())
                    error.accept(AST.getLine(), d.getID() + " is not an array and we are trying to index it.");

                //TODO have to check if an array is being assigned to an array!
            }
        }
    }

    private void callConstraints(IdStatement idStatement) {
        FuncDeclaration function = (FuncDeclaration) idStatement.getDecl();
        Expression args = (Expression) ((CallStatementTail) idStatement.getId_stmt_tail()).getCall_tail();
        ParamDeclaration currentParam = function.getParams();
        Expression currentArg = args;
        int counter = 0;
        while (currentArg != null && currentParam != null) {
            counter++;
            if (currentArg.getType() != currentParam.getType()) {
                if (currentArg.getType() != Type.ERROR)
                    error.accept(idStatement.getLine(), idStatement.getIdToken().name +
                            ", Argument #" + counter + " : Expected argument of type " + currentParam.getType()
                            + ", received argument of type " + currentArg.getType());
                else {

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

        if (currentArg == null && currentParam != null) {
            error.accept(idStatement.getLine(), "Function call " + idStatement.getIdToken().name
                    + " has not enough arguments provided.");
        } else if (currentParam == null && currentArg != null) {
            error.accept(idStatement.getLine(), "Function call " + idStatement.getIdToken().name
                    + " has too many arguments provided.");
        }
    }

    public void analyze(StatementTail AST) {
        output.accept(AST.getLine() + ": analyze StatementTail\n");
        if (AST instanceof AssignStatementTail)
            analyze((AssignStatementTail) AST);
        else if (AST instanceof CallStatementTail)
            analyze((CallStatementTail) AST);
        else if (AST == null) {System.out.println("AST = [" + AST + "]");}
    }

    public void analyze(AssignStatementTail AST) {
        output.accept(AST.getLine() + ": analyze AssignStatementTail\n");
        if (AST.getAddExpression() != null)
            analyze(AST.getAddExpression());
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

    public void analyze(CompoundStatement AST) {
        output.accept(AST.getLine() + ": analyze CompoundStatement\n");
        enteringCompoundStmt();

        Declaration d = AST.getDeclarations();
        while (d != null) {
            addDeclaration(d);
            d = (Declaration) d.getNextNode();
        }

        Statement stmt = AST.getStatements();

        while (stmt != null) {
            analyze(stmt);
            stmt = (Statement) stmt.getNextNode();
        }

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
            error.accept(AST.getLine(), "Exit statement not within loop");
            foundError = true;
        }
    }

    private boolean isInsideLoop() {
        return !currentLoop.empty();
    }

    public void analyze(ContinueStatement AST) {
        output.accept(AST.getLine() + ": analyze ContinueStatement\n");
        if (!isInsideLoop()) {
            error.accept(AST.getLine(), "Continue statement not within loop");
            foundError = true;
        }
    }

    public void analyze(ReturnStatement AST) {
        output.accept(AST.getLine() + ": analyze ReturnStatement\n");
        analyze(AST.getReturnValue());

        if (currentFuncDecl == null)
            error.accept(AST.getLine(), "Error, return statement not within function.");
        if (AST.getReturnValue().getType() != currentFuncDecl.getType())
            error.accept(AST.getLine(), "Error, return type does not match functions type.");
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
                    // semantic error
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
//            if (t == ERROR || t2 == ERROR) {
//                AST.setType(ERROR);
//            } else {
//                final List<Type> operandTypes = AST.getRelop().getOperandTypes();
//                if (t != t2 || (!operandTypes.contains(t) || !operandTypes.contains(t2))) {
//                    foundError = true;
//                    error.accept(AST.getLine(),"Expression type mismatch");
//                    AST.setType(ERROR);
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
            t = ERROR;
            foundError = true;
            error.accept(AST.getLine(), "Cannot urinary minus a boolean!");
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

        MultOpFactor next = (MultOpFactor) AST.getFactor().getNextNode();
        if (next != null) {
            Type t2 = analyze(next);

            AST.setType(typeCheck(t, t2, next.getMultOp().getOperandTypes()));

            isStatic &= next.isStatic();
        }

        AST.setStatic(isStatic);

        return t;
    }

    public Type analyze(SubExpression AST) {
        output.accept(AST.getLine() + ": analyze SubExpression\n"); //Nothing to analyze here

        if (AST instanceof IdFactor)
            return analyze((IdFactor) AST);

        else if (AST instanceof LiteralBool)
            return analyze((LiteralBool) AST);

        else if (AST instanceof LiteralNum)
            return analyze((LiteralNum) AST);

        else if (AST instanceof AddOpTerm)
            return analyze(((AddOpTerm) AST).getTerm());

        else if (AST instanceof MinusExpression)
            return INT;

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


        return ERROR;
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
            return ERROR;
        return BOOL;
    }

    public Type analyze(IdFactor AST) {
        output.accept(AST.getLine() + ": analyze IdFactor\n");

        AST.setStatic(false);

        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));

        if (AST.getIdTail() instanceof CallStatementTail)
            analyze((CallStatementTail) AST.getIdTail());

        else if (AST.getIdTail() instanceof AddExpression) {
            analyze((AddExpression) AST.getIdTail());

            if (!((AddExpression) AST.getIdTail()).isStatic()) {
                foundError = true;
                error.accept(AST.getLine(), "Non static expression in array index");
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

    private Declaration getDeclaration(int id) {
        SymbolTableEntry d = symbolTable.get(id);

        if (d == null) {
            regError.accept("No declaration for this id: " + id);
            return errorDeclaration;
        }

        return (Declaration) d.getNode();
    }

    public void addDeclaration(Declaration d) {
        symbolTable.push(new SymbolTableEntry(d.getID().getAttrValue(), d));
    }

    @Override
    public void analyze(ASTNode AST) {
//        if (AST == null) {
//            System.out.println("AST NULL!?");
//        } else {
        output.accept(AST.getLine() + ": analyze ASTNode\n");
//        }
    }


    public Type typeCheck(Type t, Type t2, List<Type> operandTypes) {
        //If any of them are already ERROR then we don't report the error because it has already been reported.
        if (t == ERROR || t2 == ERROR)
            return ERROR;

        //If the types do not match or the operand types available do not contain one of these operators then report error
        if (t != t2 || (!operandTypes.contains(t) || !operandTypes.contains(t2))) {
            foundError = true;
            regError.accept("Expression type mismatch");
            return ERROR;
        }
        //Everything is fine, return the type
        return t;
    }

}
