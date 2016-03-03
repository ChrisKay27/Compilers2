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
import java.util.function.Consumer;

import static parser.Type.*;

/**
 * Created by Carston on 2/27/2016.
 */
public class SemanticAnalyzer implements SemAnalInter {

    private ASTNode astRoot;
    private SymbolTable symbolTable;
    private final Consumer<String> output;
    private Consumer<String> error;

    private boolean foundError;
    private boolean traceEnabled;

    private FuncDeclaration currentFuncDecl;

    //Keeps track of any loops that we are currently in (as we look through the tree) to see if an exit or continue is not
    //inside of a loop
    private Stack<LoopStatement> currentLoop = new Stack<>();

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public SemanticAnalyzer(ASTNode astRoot, Consumer<String> output, Consumer<String> error){
        this.astRoot = astRoot;
        this.output = output;
        this.error = error;

        this.symbolTable = new SymbolTable();
    }


    public void startSemAnal(Declaration AST) {

        //Library functions
        addDeclaration(new FuncDeclaration(0,INT,new Token(TokenType.ID,0),null,null));
        addDeclaration(new FuncDeclaration(0,Type.VOID,new Token(TokenType.ID,1),new ParamDeclaration(0,INT,new Token(TokenType.ID,4),false,false),null));
        addDeclaration(new FuncDeclaration(0,Type.BOOL,new Token(TokenType.ID,2),null,null));
        addDeclaration(new FuncDeclaration(0,Type.VOID,new Token(TokenType.ID,3),new ParamDeclaration(0,BOOL,new Token(TokenType.ID,4),false,false),null));


        Declaration n = AST;
        do {
            addDeclaration(n);
            n = (Declaration) n.getNextNode();
        }
        while (n != null);

        n = AST;
        do {
            if( n instanceof VarDeclaration)
                analyze((VarDeclaration)n);
            else
                analyze((FuncDeclaration)n);

            n = (Declaration) n.getNextNode();
        }
        while (n != null);

    }

//    public void analyze(ASTNode AST) {

//        System.out.println("Class of this ASTNode: " + AST.getClass().getSimpleName());
//    }

    public void analyze(FuncDeclaration AST){
        System.out.println("analyze FuncDeclaration");

        enteringFuncDeclaration(AST);

        analyze(AST.getParams());

        analyze(AST.getBody());
        functionConstraints(AST.getType(), AST.getBody());

        leavingFuncDeclaration();
    }

    private void functionConstraints(Type type, Statement statement) {
        boolean returnFound = false;
        while (statement != null) {
            statement = (Statement) statement.getNextNode();
            if (statement instanceof ReturnStatement) {
                returnFound = true;
                ReturnStatement returnStatement = (ReturnStatement) statement;
                if (returnStatement.getType() != type) {
                    error.accept("The returned value must match the return type of the function.");
                }
            }
        }
        if (!returnFound && type != Type.VOID) {
            error.accept("No return statement found.");
        }
    }


    private void enteringFuncDeclaration(FuncDeclaration AST) {
        System.out.println("enteringFuncDeclaration");
        enteringCompoundStmt();
        if( currentFuncDecl != null )
            throw new WTFException("No functions are allowed to be inside other functions");
        currentFuncDecl = AST;
    }
    private void leavingFuncDeclaration() {
        output.accept("leavingFuncDeclaration");
        currentFuncDecl = null;
        leavingCompoundStmt();
    }

    public void analyze(ParamDeclaration AST) {
		output.accept("analyze ParamDeclaration");
        while (AST != null) {
            if( AST.getType() != VOID)
                addDeclaration(AST);
            AST = (ParamDeclaration) AST.getNextNode();
        }
    }


    public void analyze(VarDeclaration AST) {
		output.accept("analyze VarDeclaration");
        //Dec already added in first sweep phase
    }

    public void analyze(Statement AST) {
		output.accept("analyze Statement");
        if( AST instanceof IdStatement)
            analyze((IdStatement) AST);
        if( AST instanceof IfStatement)
            analyze((IfStatement) AST);
        if( AST instanceof CompoundStatement)
            analyze((CompoundStatement) AST);
        if( AST instanceof LoopStatement)
            analyze((LoopStatement) AST);
        if( AST instanceof ContinueStatement)
            analyze((ContinueStatement) AST);
        if( AST instanceof ExitStatement)
            analyze((ExitStatement) AST);
        if( AST instanceof ReturnStatement)
            analyze((ReturnStatement) AST);
        if( AST instanceof BranchStatement)
            analyze((BranchStatement) AST);
        if( AST instanceof CaseStatement)
            analyze((CaseStatement) AST);
        else if( AST instanceof NullStatement)
            analyze((NullStatement) AST);
    }

    public void analyze(IdStatement AST) {
		output.accept("analyze IdStatement");
        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));
        analyze(AST.getId_stmt_tail());

        Declaration d = AST.getDecl();
        if( d instanceof FuncDeclaration) {
            if ((AST.getId_stmt_tail() instanceof AssignStatementTail))
                error.accept(d.getID() + " is a function declaration and it is being assigned to.");
        }


        if( d instanceof VarDeclaration){
            VarDeclaration varDec = (VarDeclaration) d;

            if(AST.getId_stmt_tail() instanceof CallStatementTail)
                error.accept(d.getID() + " is a var declaration and its being called as a function.");
            else{
                AssignStatementTail assignTail = (AssignStatementTail) AST.getId_stmt_tail();

                //If we are trying to index this variable and it is not an array...
                if( assignTail.getAddExpression() != null && !varDec.isAnArray() )
                    error.accept(d.getID()+" is not an array and we are trying to index it.");

                //TODO have to check if an array is being assigned to an array!
            }
        }
    }

    public void analyze(StatementTail AST){
		output.accept("analyze StatementTail");
        if( AST instanceof AssignStatementTail)
            analyze((AssignStatementTail) AST);
        else if( AST instanceof CallStatementTail)
            analyze((CallStatementTail) AST);
    }

    public void analyze(AssignStatementTail AST) {
		output.accept("analyze AssignStatementTail");
        if (AST.getAddExpression() != null)
            analyze(AST.getAddExpression());
        analyze(AST.getExp());
    }

    public void analyze(IfStatement AST) {
		output.accept("analyze IfStatement");
        analyze(AST.getExpression());
        analyze(AST.getStatement());
        Statement elseStatement = AST.getElseStatement();
        if(elseStatement != null) analyze(elseStatement);
    }

    public void analyze(CallStatementTail AST) {
		output.accept("analyze CallStatementTail");
        analyze(AST.getCall_tail());
    }


    public void analyze(CompoundStatement AST) {
		output.accept("analyze CompoundStatement");
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
		output.accept("analyze LoopStatement");
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
		output.accept("analyze ExitStatement");
        if(! isInsideLoop() ){
            error.accept("Exit statement not within loop");
            foundError = true;
        }
    }

    private boolean isInsideLoop() {
        return !currentLoop.empty();
    }

    public void analyze(ContinueStatement AST) {
		output.accept("analyze ContinueStatement");
        if(! isInsideLoop() ){
            error.accept("Continue statement not within loop");
            foundError = true;
        }
    }

    public void analyze(ReturnStatement AST){
		output.accept("analyze ReturnStatement");
        analyze(AST.getReturnValue());

        if( currentFuncDecl == null )
            error.accept("Error, return statement not within function.");
        if(AST.getReturnValue().getType() != currentFuncDecl.getType())
            error.accept("Error, return type does not match functions type.");

    }

    public void analyze(NullStatement AST){
		output.accept("analyze NullStatement");
    }

    public void analyze(BranchStatement AST) {
		output.accept("analyze BranchStatement");
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

    public Type analyze(Expression AST){
		output.accept("analyze Expression");

        Type t = analyze(AST.getAddExp());
        AST.setType(t);

        boolean isStatic = AST.getAddExp().isStatic();

        if( AST.getAddExp2() != null ) {
            Type t2 = analyze(AST.getAddExp2());

            AST.setType(typeCheck(t,t2,AST.getRelop().getOperandTypes()));

            isStatic &= AST.getAddExp2().isStatic();
//
//            if (t == ERROR || t2 == ERROR) {
//                AST.setType(ERROR);
//            } else {
//                final List<Type> operandTypes = AST.getRelop().getOperandTypes();
//                if (t != t2 || (!operandTypes.contains(t) || !operandTypes.contains(t2))) {
//                    foundError = true;
//                    error.accept("Expression type mismatch");
//                    AST.setType(ERROR);
//                } else
//                    AST.setType(t);
//            }
        }

        AST.setStatic(isStatic);

        return AST.getType();
    }


    public Type analyze(AddExpression AST){
		output.accept("analyze AddExpression");

        Type t = analyze(AST.getTerm());
        AST.setType(t);

        boolean isStatic = AST.getTerm().isStatic();

        if( t != INT && AST.isUminus()){
            t = ERROR;
            foundError = true;
            error.accept("Cannot urinary minus a boolean!");
        }

        AddOpTerm next = AST.getNextNode();
        while(next != null){
            Type t2 = analyze(next);

            AST.setType(typeCheck(t,t2,next.getAddOp().getOperandTypes()));

            isStatic &= next.isStatic();

            next = AST.getNextNode();
        }

        AST.setStatic(isStatic);

        return t;
    }


    public Type analyze(Term AST){
		output.accept("analyze Term");

        Type t = analyze(AST.getFactor());
        AST.setType(t);

        boolean isStatic = AST.getFactor().isStatic();

        MultOpFactor next = AST.getNextNode();
        while(next != null){
            Type t2 = analyze(next);

            AST.setType(typeCheck(t,t2,next.getMultOp().getOperandTypes()));

            isStatic &= next.isStatic();

            next = AST.getNextNode();
        }

        AST.setStatic(isStatic);

        return t;
    }

    public Type analyze(SubExpression AST){
		output.accept("analyze SubExpression"); //Nothing to analyze here

        if( AST instanceof IdFactor)
            return analyze((IdFactor) AST);

        else if( AST instanceof LiteralBool)
            return analyze((LiteralBool) AST);

        else if( AST instanceof LiteralNum)
            return analyze((LiteralNum) AST);

		else if( AST instanceof AddOpTerm)
			return analyze(((AddOpTerm) AST).getTerm());

		else if( AST instanceof MinusExpression)
			return INT;

		else if( AST instanceof MultOpFactor)
			return analyze((MultOpFactor) AST);

        else if( AST instanceof NotNidFactor)
            return analyze((NotNidFactor) AST);

		else if( AST instanceof Term)
			return analyze((Term) AST);

        else if( AST instanceof AddExpression)
            return analyze((AddExpression) AST);

        else if( AST instanceof Expression)
            return analyze((Expression) AST);



        return ERROR;
    }


    public Type analyze(LiteralBool AST){
		output.accept("analyze LiteralBool"); //Nothing to analyze here
        return BOOL;
    }
    public Type analyze(LiteralNum AST){
		output.accept("analyze LiteralNum"); //Nothing to analyze here
        return INT;
    }

    public Type analyze(MultOpFactor AST){
		output.accept("analyze MultOpFactor");
        return analyze(AST.getFactor());
    }

    public Type analyze(NotNidFactor AST){
		output.accept("analyze NotNidFactor");
        
        Type t = analyze(AST.getFactor());
        if( t != BOOL )
            return ERROR;
        return BOOL;
    }

    public Type analyze(IdFactor AST){
        output.accept("analyze IdFactor");

        AST.setStatic(false);

        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));

        if( AST.getIdTail() instanceof CallStatementTail)
            analyze((CallStatementTail) AST.getIdTail());

        else if(AST.getIdTail() instanceof AddExpression) {
            analyze((AddExpression) AST.getIdTail());
            
            if(! ((AddExpression) AST.getIdTail()).isStatic() ){
                foundError = true;
                error.accept(AST.getLine()+": Non static expression in array index");
            }
        }

        return AST.getDecl().getType();
    }




    private void enteringCompoundStmt() {
        output.accept("enteringCompoundStmt");
        symbolTable.enterFrame();
    }


    private void leavingCompoundStmt() {
        output.accept("leavingCompoundStmt");
        symbolTable.leaveFrame();
    }


    private Declaration errorDeclaration = new Declaration(-1,Type.ERROR,null);

    private Declaration getDeclaration(int id) {
        SymbolTableEntry d = symbolTable.get(id);

        if( d == null ){
            error.accept("No declaration for this id: " + id);
            return errorDeclaration;
        }
        return (Declaration) d.getNode();
    }

    public void addDeclaration(Declaration d) {
        symbolTable.push(new SymbolTableEntry(d.getID().getAttrValue(),d));
    }

    @Override
    public void analyze(ASTNode AST) {
		output.accept("analyze ASTNode");        
    }


    public Type typeCheck(Type t, Type t2, List<Type> operandTypes){
        //If any of them are already ERROR then we don't report the error because it has already been reported.
        if( t == ERROR || t2 == ERROR )
            return ERROR;

        //If the types do not match or the operand types available do not contain one of these operators then report error
        if( t != t2 || (!operandTypes.contains(t) || !operandTypes.contains(t2))) {
            foundError = true;
            error.accept("Expression type mismatch");
            return ERROR;
        }
        //Everything is fine, return the type
        return t;
    }


}
