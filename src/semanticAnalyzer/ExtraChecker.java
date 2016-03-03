package semanticAnalyzer;

import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.ParamDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.*;
import parser.grammar.statements.*;
import util.WTFException;

import java.util.function.Consumer;

/**
 * Created by Chris on 2/27/2016.
 */
public class ExtraChecker {

    private ASTNode astRoot;
    private Consumer<String> error;

    private FuncDeclaration currentFuncDecl;

    public ExtraChecker(ASTNode astRoot, Consumer<String> error){
        this.astRoot = astRoot;
        this.error = error;

        startTypeAnal(astRoot);
    }


    public void startTypeAnal(ASTNode AST){

        ASTNode n = AST;
        do{
            analyze(n);
        }
        while((n = n.getNextNode()) != null);

    }

    private void analyze(ASTNode AST){

    }

    private void analyze(FuncDeclaration AST){
        enteringFuncDeclation(AST);

        analyze(AST.getBody());

        leavingFuncDeclaration();
    }


    private void enteringFuncDeclation(FuncDeclaration AST) {
        currentFuncDecl = AST;
    }
    private void leavingFuncDeclaration() {
        currentFuncDecl = null;
    }


    private void analyze(IdStatement AST){


    }

    private void analyze(AssignStatementTail AST){

    }

    private void analyze(CompoundStatement AST){

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

    }


    private void analyze(AddExpression AST){

    }

    private void analyze(Term AST){

    }

    private void analyze(LiteralBool AST){ //Nothing to analyze here
    }
    private void analyze(LiteralNum AST){ //Nothing to analyze here
    }

    private void analyze(NotNidFactor AST){
        analyze(AST.getFactor());
    }

    private void analyze(IdFactor AST){

    }




}
