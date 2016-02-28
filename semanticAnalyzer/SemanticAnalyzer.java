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

    public SemanticAnalyzer(ASTNode astRoot) {
        this.astRoot = astRoot;
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

    private void analyze(FuncDeclaration AST) {
        enteringCompoundStmt();

        analyze(AST.getParams());

        analyze(AST.getBody());

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
    }

    private void analyze(AssignStatementTail AST) {
        if (AST.getAddExpression() != null)
            analyze(AST.getAddExpression());

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

    private void analyze(ReturnStatement AST) {
        analyze(AST.getReturnValue());
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

    private void analyze(NullStatement AST) {

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

    private void analyze(Expression AST) {
        analyze(AST.getAddExp());
        analyze(AST.getAddExp2());
    }

    private void analyze(IdFactor AST) {
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
