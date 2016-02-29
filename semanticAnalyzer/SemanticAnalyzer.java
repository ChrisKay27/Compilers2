package semanticAnalyzer;

import parser.Type;
import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.ParamDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.*;
import parser.grammar.statements.*;
import util.WTFException;

import java.util.function.Consumer;

import static parser.Type.BOOL;
import static parser.Type.ERROR;
import static parser.Type.INT;

/**
 * Created by Carston on 2/27/2016.
 */
public class SemanticAnalyzer implements SemAnalInter {

    private ASTNode astRoot;
    private SymbolTable symbolTable;
    private Consumer<String> error;

    private FuncDeclaration currentFuncDecl;
    private boolean foundError;

    public SemanticAnalyzer(ASTNode astRoot, Consumer<String> error){
        this.astRoot = astRoot;
        this.error = error;

        this.symbolTable = new SymbolTable();
    }


    public void startSemAnal(Declaration AST) {

        Declaration n = AST;
        do {
            addDeclaration(n);
        }
        while ((n = (Declaration) n.getNextNode()) != null);

        n = AST;
        do {
            if( n instanceof VarDeclaration )
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
        enteringFuncDeclation(AST);

        analyze(AST.getParams());

        analyze(AST.getBody());

        leavingFuncDeclaration();
    }


    private void enteringFuncDeclation(FuncDeclaration AST) {
        enteringCompoundStmt();
        currentFuncDecl = AST;
    }
    private void leavingFuncDeclaration() {
        currentFuncDecl = null;
        leavingCompoundStmt();
    }

    public void analyze(ParamDeclaration AST) {
        while (AST != null) {
            addDeclaration(AST);
            AST = (ParamDeclaration) AST.getNextNode();
        }
    }


    public void analyze(VarDeclaration AST) {
        //Dec already added in first sweep phase
    }

    public void analyze(Statement AST) {
        if( AST instanceof IdStatement )
            analyze((IdStatement) AST);
        if( AST instanceof IfStatement )
            analyze((IfStatement) AST);
        if( AST instanceof CompoundStatement )
            analyze((CompoundStatement) AST);
        if( AST instanceof LoopStatement )
            analyze((LoopStatement) AST);
        if( AST instanceof ContinueStatement )
            analyze((ContinueStatement) AST);
        if( AST instanceof ExitStatement )
            analyze((ExitStatement) AST);
        if( AST instanceof ReturnStatement )
            analyze((ReturnStatement) AST);
        if( AST instanceof BranchStatement )
            analyze((BranchStatement) AST);
        if( AST instanceof CaseStatement )
            analyze((CaseStatement) AST);
        else if( AST instanceof NullStatement )
            analyze((NullStatement) AST);
    }

    public void analyze(IdStatement AST) {
        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));
        analyze(AST.getId_stmt_tail());

        Declaration d = AST.getDecl();
        if( d instanceof FuncDeclaration ) {
            if ((AST.getId_stmt_tail() instanceof AssignStatementTail))
                error.accept(d.getID() + " is a function declaration and it is being assigned to.");
        }


        if( d instanceof VarDeclaration ){
            VarDeclaration varDec = (VarDeclaration) d;

            if(AST.getId_stmt_tail() instanceof CallStatementTail )
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
        if( AST instanceof AssignStatementTail )
            analyze((AssignStatementTail) AST);
        else if( AST instanceof CallStatementTail )
            analyze((CallStatementTail) AST);
    }

    public void analyze(AssignStatementTail AST) {
        if (AST.getAddExpression() != null)
            analyze(AST.getAddExpression());
        analyze(AST.getExp());
    }

    public void analyze(IfStatement AST) {
        analyze(AST.getExpression());
        analyze(AST.getStatement());
        Statement elseStatement = AST.getElseStatement();
        if(elseStatement != null) analyze(elseStatement);
    }

    public void analyze(CallStatementTail AST) {
       analyze(AST.getCall_tail());
    }


    public void analyze(CompoundStatement AST) {
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
        Statement statement = AST.getStatement();
        loopConstraints(statement);
        analyze(statement);
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

    }

    public void analyze(ContinueStatement AST) {

    }

    public void analyze(ReturnStatement AST){
        analyze(AST.getReturnValue());

        if( currentFuncDecl == null )
            error.accept("Error, return statement not within function.");
        if(AST.getReturnValue().getType() != currentFuncDecl.getType())
            error.accept("Error, return type does not match functions type.");

    }

    public void analyze(NullStatement AST){
        
    }

    public void analyze(BranchStatement AST) {
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
        Type t = analyze(AST.getAddExp());
        Type t2 = analyze(AST.getAddExp2());
        if( t != t2 ) {
            foundError = true;
            error.accept("Expression type mismatch");
            AST.setType(ERROR);
        }else
            AST.setType(t);
        return AST.getType();
    }


    public Type analyze(AddExpression AST){
        Type t = analyze(AST.getTerm());

        AddExpression next = (AddExpression) AST.getNextNode();
        while(next != null){
            Type t2 = analyze(next);

            if( t != t2 && t != ERROR) {
                foundError = true;
                t = ERROR;
                error.accept("Add Expression type mismatch");
            }

            next = (AddExpression) AST.getNextNode();
        }
        AST.setType(t);
        return t;
    }

    public Type analyze(Term AST){
        Type t = analyze(AST.getFactor());

        Factor next = (Factor) AST.getNextNode();
        while(next != null){
            Type t2 = analyze(next);

            if( t != t2 && t != ERROR) {
                foundError = true;
                t = ERROR;
                error.accept("Add Expression type mismatch");
            }
            next = (Factor) AST.getNextNode();
        }
        return t;
    }

    public Type analyze(Subexpression AST){ //Nothing to analyze here

        if( AST instanceof AddExpression )
            return analyze((AddExpression) AST);
		if( AST instanceof AddOpTerm )
			return analyze(((AddOpTerm) AST).getTerm());
		if( AST instanceof Expression )
			return analyze((Expression) AST);
		if( AST instanceof IdFactor )
            return analyze((IdFactor) AST);
        if( AST instanceof LiteralBool )
            return analyze((LiteralBool) AST);
        if( AST instanceof LiteralNum )
			return analyze((LiteralNum) AST);
		if( AST instanceof MinusExpression )
			return INT;//analyze((MinusExpression) AST);
		if( AST instanceof MultOpFactor )
			return analyze((MultOpFactor) AST);
        if( AST instanceof NotNidFactor )
            return analyze((NotNidFactor) AST);
		if( AST instanceof Term )
			return analyze((Term) AST);

        return ERROR;
    }




    public Type analyze(LiteralBool AST){ //Nothing to analyze here
        return BOOL;
    }
    public Type analyze(LiteralNum AST){ //Nothing to analyze here
        return INT;
    }

    public Type analyze(MultOpFactor AST){
        return analyze(AST.getFactor());
    }

    public Type analyze(NotNidFactor AST){
        Type t = analyze(AST.getFactor());
        if( t != BOOL )
            return ERROR;
        return BOOL;
    }

    public Type analyze(IdFactor AST){
        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));
        analyze(AST.getIdTail());
        return AST.getDecl().getType();
    }


    private void enteringCompoundStmt() {
        symbolTable.enterFrame();
    }


    private void leavingCompoundStmt() {
        symbolTable.leaveFrame();
    }


    private Declaration getDeclaration(int id) {
        SymbolTableEntry d = symbolTable.get(id);
        if( d == null ){
            error.accept("");
        }
        return null;
    }

    public void addDeclaration(Declaration d) {
        symbolTable.push(new SymbolTableEntry(d.getID().getAttrValue(),d));
    }

    @Override
    public void analyze(ASTNode AST) {        
    }
}
