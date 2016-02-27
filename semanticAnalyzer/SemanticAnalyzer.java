package semanticAnalyzer;

import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.ParamDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.Expression;
import parser.grammar.expressions.IdFactor;
import parser.grammar.statements.*;
import util.WTFException;

/**
 * Created by Carston on 2/27/2016.
 */
public class SemanticAnalyzer {

    private ASTNode astRoot;
    private SymbolTable symbolTable;

    public SemanticAnalyzer(ASTNode astRoot){
        this.astRoot = astRoot;
        this.symbolTable = new SymbolTable();

        startSemAnal(astRoot);
    }


    public void startSemAnal(ASTNode AST){

        ASTNode n = AST;
        do{
            addDeclaration((Declaration) n);
        }
        while((n = n.getNextNode()) != null);

        n = AST;
        do{
            analyze(n);
        }
        while((n = n.getNextNode()) != null);

    }

    private void analyze(ASTNode AST){
        throw new WTFException("Finish implementing the methods. Look for a method for " + AST.getClass().getSimpleName());
    }

    private void analyze(FuncDeclaration AST){
        enteringCompoundStmt();

        analyze(AST.getParams());

        analyze(AST.getBody());

        leavingCompoundStmt();
    }

    private void analyze(ParamDeclaration AST){
        while( AST != null ) {
            addDeclaration(AST);
            AST = (ParamDeclaration) AST.getNextNode();
        }
    }


    private void analyze(VarDeclaration AST){
        //Dec already added in first sweep phase
    }


    private void analyze(IdStatement AST){
        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));
        analyze(AST.getId_stmt_tail());
    }

    private void analyze(AssignStatementTail AST){
        if(AST.getAddExpression() != null)
            analyze(AST.getAddExpression());

    }

    private void analyze(CompoundStatement AST){
        enteringCompoundStmt();

        Declaration d = AST.getDeclarations();
        while( d != null ) {
            addDeclaration(d);
            d = (Declaration) d.getNextNode();
        }

        Statement stmt = AST.getStatements();

        while( stmt != null ){
            analyze(stmt);
            stmt = (Statement) stmt.getNextNode();
        }

        leavingCompoundStmt();
    }




    private void analyze(LoopStatement AST){


    }

    private void analyze(ExitStatement AST){


    }

    private void analyze(ContinueStatement AST){

    }

    private void analyze(ReturnStatement AST){

    }

    private void analyze(NullStatement AST){

    }

    private void analyze(BranchStatement AST){

    }




    private void analyze(Expression AST){
        analyze(AST.getAddExp());
        analyze(AST.getAddExp2());
    }

    private void analyze(IdFactor AST){
        AST.setDecl(getDeclaration(AST.getIdToken().getAttrValue()));
        analyze(AST.getIdTail());
    }





    private void enteringCompoundStmt() {
    }


    private void leavingCompoundStmt() {
    }


    private Declaration getDeclaration(int id) {
        return null;
    }

    private void addDeclaration(Declaration d) {

    }
}
