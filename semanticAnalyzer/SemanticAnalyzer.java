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
public class SemanticAnalyzer {

    private ASTNode astRoot;
    private SymbolTable symbolTable;
    private Consumer<String> error;

    private FuncDeclaration currentFuncDecl;
    private boolean foundError;

    public SemanticAnalyzer(ASTNode astRoot, Consumer<String> error){
        this.astRoot = astRoot;
        this.error = error;

        this.symbolTable = new SymbolTable();

        startSemAnal(astRoot);

    }


    public void startSemAnal(ASTNode AST) {

        ASTNode n = AST;
        do {
            addDeclaration((Declaration) n);
        }
        while ((n = n.getNextNode()) != null);

        n = AST;
        do {
            analyze(n);
        }
        while ((n = n.getNextNode()) != null);

    }

    private void analyze(ASTNode AST) {
        throw new WTFException("Finish implementing the methods. Look for a method for " + AST.getClass().getSimpleName());
    }

    private void analyze(FuncDeclaration AST){
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

    private void analyze(ParamDeclaration AST) {
        while (AST != null) {
            addDeclaration(AST);
            AST = (ParamDeclaration) AST.getNextNode();
        }
    }


    private void analyze(VarDeclaration AST) {
        //Dec already added in first sweep phase
    }


    private void analyze(IdStatement AST) {
        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));
        analyze(AST.getId_stmt_tail());

        Declaration d = AST.getDecl();
        if( d instanceof FuncDeclaration && (AST.getId_stmt_tail() instanceof AssignStatementTail) )
            error.accept(d.getID() + " is a function declaration and it is being assigned to.");

        if( d instanceof VarDeclaration ){
            VarDeclaration varDec = (VarDeclaration) d;

            if(AST.getId_stmt_tail() instanceof CallStatementTail )
                error.accept(d.getID() + " is a var declaration and its being called as a function.");
            else{
                AssignStatementTail assignTail = (AssignStatementTail) AST.getId_stmt_tail();

                //If we are trying to index this variable and it is not an array...
                if( assignTail.getAddExpression() != null && !varDec.isAnArray() )
                    error.accept(d.getID()+" is not an array and we are trying to index it.");

                //TODO have to check if an array is being assigned to an array! But currently we dont know what type the
                //expression in assign tail is.
            }
        }
    }

    private void analyze(AssignStatementTail AST) {
        if (AST.getAddExpression() != null)
            analyze(AST.getAddExpression());
        analyze(AST.getExp());
    }

    private void analyze(IfStatement AST) {
        analyze(AST.getExpression());
        analyze(AST.getStatement());
        Statement elseStatement = AST.getElseStatement();
        if(elseStatement != null) analyze(elseStatement);
    }

    private void analyze(CallStatementTail AST) {
       analyze(AST.getCall_tail());
    }


    private void analyze(CompoundStatement AST) {
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


    private void analyze(LoopStatement AST) {
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


    private void analyze(ExitStatement AST) {

    }

    private void analyze(ContinueStatement AST) {

    }

    private void analyze(ReturnStatement AST){
        analyze(AST.getReturnValue());

        if( currentFuncDecl == null )
            error.accept("Error, return statement not within function.");
        if(AST.getReturnValue().getType() != currentFuncDecl.getType())
            error.accept("Error, return type does not match functions type.");

    }

    private void analyze(NullStatement AST){

    }

    private void analyze(BranchStatement AST) {
        branchConstraints(AST.getCaseStmt());
        analyze(AST.getAddexp());
        analyze((CaseStatement) AST.getCaseStmt());
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

    private void analyze(CaseStatement statement) {
        analyze(statement.getStatement()); // statement for this case
        analyze(statement.getNextNode()); // next case in branch statement
    }

    private void analyze(Expression AST){
        Type t = analyze(AST.getAddExp());
        Type t2 = analyze(AST.getAddExp2());
        if( t != t2 ) {
            foundError = true;
            error.accept("Expression type mismatch");
            AST.setType(ERROR);
        }else
            AST.setType(t);
    }


    private Type analyze(AddExpression AST){
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

    private Type analyze(Term AST){
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

    private Type analyze(Subexpression AST){ //Nothing to analyze here
        return ERROR;
    }

    private Type analyze(LiteralBool AST){ //Nothing to analyze here
        return BOOL;
    }
    private Type analyze(LiteralNum AST){ //Nothing to analyze here
        return INT;
    }

    private Type analyze(NotNidFactor AST){
        Type t = analyze(AST.getFactor());
        if( t != BOOL )
            return ERROR;
        return BOOL;
    }

    private Type analyze(IdFactor AST){
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

    private void addDeclaration(Declaration d) {
        symbolTable.push(new SymbolTableEntry(d.getID().getAttrValue(),d));
    }
}
