package tupleGeneration;

import parser.Type;
import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.*;
import parser.grammar.statements.*;
import util.WTFException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Carston on 2/27/2016.
 */
public class QuadrupleGenerator {

    private final Consumer<String> output;
    private final Consumer<String> regError;
    private ASTNode astRoot;
    private BiConsumer<Integer, String> error;
    private boolean foundError;
    private boolean traceEnabled;

    private FuncDeclaration currentFuncDecl;
    private static Declaration errorDeclaration = new Declaration(-1, Type.UNIV, null);
    private boolean returnStatementFound;

    private int numberOfGlobals;
    private int labelCounter;
    private int tempCounter;

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public QuadrupleGenerator(ASTNode astRoot, Consumer<String> output, Consumer<String> error, BiConsumer<Integer, String> lineError) {
        this.astRoot = astRoot;
        this.output = output;
        this.regError = error;
        this.error = lineError;

    }


    public boolean startCodeGeneration(Declaration AST) {

        Declaration current = AST;
        FuncDeclaration lastFunction = null;
        do {
            current.setLevel(0);
            //TODO do something with this
            current = current.getNextNode();
        }
        while (current != null);

        current = AST;
        Declaration prev = current; // POSSIBLE BUG?
        current.setCode("start " + numberOfGlobals + ",-,-");
        do {
            if (current instanceof VarDeclaration) {
                generate((VarDeclaration) current);
            } else {
                generate((FuncDeclaration) current);
                lastFunction = (FuncDeclaration) current;
            }
            prev.appendCode(current.getCode());
            prev = current;
            current = (Declaration) current.getNextNode();

        }
        while (current != null);

        output.accept("\n" + AST.getCode());

        return !foundError;
    }

    public void generate(FuncDeclaration AST) {
        output.accept(AST.getLine() + ": generating code for FuncDeclaration\n");

        AST.setCode("fun " + AST.getID().name + "," + AST.getNumberOfLocals() + ",-");
//        generate(AST.getParams());

        generate(AST.getBody());
        AST.appendCode(AST.getBody().getCode());
    }

//    // I don't think we need to generate any code for param declarations
//    public void generate(ParamDeclaration AST) {
//        output.accept(AST.getLine() + ": generating code for ParamDeclaration\n");
//        while (AST != null) {
//            //TODO do something with this param dec
//            AST = (ParamDeclaration) AST.getNextNode();
//        }
//    }

//    // I don't think we need to generate any code for variable declarations either
//    public void generate(VarDeclaration AST) {
//        output.accept(AST.getLine() + ": generating code for VarDeclaration\n");
//        VarDeclaration current = AST, prev = current;
//        if (AST.getLevel() != 0) {
//            while (AST != null) {
//                //TODO do something with this param dec
//                prev = current;
//                current = (VarDeclaration) current.getNextNode();
//            }
//        }
//    }

    public void generate(Statement AST) {
        output.accept(AST.getLine() + ": generating code for Statement\n");

        if (AST instanceof IdStatement)
            generate((IdStatement) AST);
        if (AST instanceof IfStatement)
            generate((IfStatement) AST);
        if (AST instanceof CompoundStatement)
            generate((CompoundStatement) AST);
        if (AST instanceof LoopStatement)
            generate((LoopStatement) AST);
        if (AST instanceof ContinueStatement)
            generate((ContinueStatement) AST);
        if (AST instanceof ExitStatement)
            generate((ExitStatement) AST);
        if (AST instanceof ReturnStatement)
            generate((ReturnStatement) AST);
        if (AST instanceof BranchStatement)
            generate((BranchStatement) AST);
        else if (AST instanceof NullStatement)
            generate((NullStatement) AST);
    }

    public void generate(IdStatement AST) {
        output.accept(AST.getLine() + ": generating code for IdStatement\n");

        String assignedValue = generate(AST.getId_stmt_tail());
        AST.appendCode(AST.getId_stmt_tail().getCode());
        AST.appendCode("asg " + assignedValue + ",-," + AST.getIdToken().name);
    }

    public String generate(StatementTail AST) {
        output.accept(AST.getLine() + ": generating code for StatementTail\n");
        if (AST instanceof AssignStatementTail)
            return generate((AssignStatementTail) AST);
        else if (AST instanceof CallStatementTail)
            return generate((CallStatementTail) AST);
        throw new WTFException("Malformed statement tail found in quadruple generation stage");
    }

    public String generate(AssignStatementTail AST) {
        output.accept(AST.getLine() + ": generating code for AssignStatementTail\n");
        String returnedValue = "";
        if (AST.getAddExpression() != null)
            returnedValue = generate(AST.getAddExpression());
        generate(AST.getExp());

        AST.setCode(AST.getExp().getCode());

        return returnedValue;
    }

    public void generate(IfStatement AST) {
        output.accept(AST.getLine() + ": generating code for IfStatement\n");
        String returnedValue = generate(AST.getExpression());
        Statement elseStatement = AST.getElseStatement();
        boolean hasElse = elseStatement != null;
        String elseLabel = getNewLabel();
        String exitLabel = (hasElse ? getNewLabel() : null);

        AST.setCode("iff " + returnedValue + ",-," + elseLabel);
        generate(AST.getStatement());
        AST.appendCode(AST.getStatement().getCode());
        if (hasElse) AST.appendCode("goto -,-," + exitLabel);
        AST.appendCode("lab " + elseLabel + ",-,-");

        if (hasElse) {
            generate(elseStatement);
            AST.appendCode(elseStatement.getCode());
            AST.appendCode("lab " + exitLabel + ",-,-");
        }
    }

    public String generate(CallStatementTail AST) {
        output.accept(AST.getLine() + ": generating code for CallStatementTail\n");

        if (AST.getCall_tail() != null) {
            Expression expression = (Expression) AST.getCall_tail();
            Expression current = expression;
            while (current != null) {

                generate(current);

                if (current.getNextNode() instanceof Expression) {
                    current = (Expression) current.getNextNode();
                } else {
                    current = null;
                }
            }
        }
        String temp = null;
        return temp;
    }

    public void generate(CompoundStatement AST) {
        output.accept(AST.getLine() + ": generating code for CompoundStatement\n");

        Declaration d = AST.getDeclarations();
        while (d != null) {
            d = d.getNextNode();
            //TODO do something with this declarations generated code
        }

        Statement stmt = AST.getStatements();

        while (stmt != null) {
            generate(stmt);
            stmt = (Statement) stmt.getNextNode();
            //TODO Do something with this statements generated code
        }
    }

    public void generate(LoopStatement AST) {
        output.accept(AST.getLine() + ": generating code for LoopStatement\n");


        Statement statement = AST.getStatement();
        generate(statement);
        //TODO do something with this statements code

    }

    public void generate(ExitStatement AST) {
        output.accept(AST.getLine() + ": generating code for ExitStatement\n");
        //TODO add jump to label?
    }


    public void generate(ContinueStatement AST) {
        output.accept(AST.getLine() + ": generating code for ContinueStatement\n");
        //TODO add jump to label?
    }

    public void generate(ReturnStatement AST) {
        output.accept(AST.getLine() + ": generating code for ReturnStatement\n");
        String temp = generate(AST.getReturnValue());
        if (AST.getReturnValue() == null) {
            AST.setCode("ret " + currentFuncDecl.getNumberOfParameters() + ",-,-");
        } else {
            AST.setCode(AST.getReturnValue().getCode());
            AST.appendCode("retv " + currentFuncDecl.getNumberOfParameters() + "," + temp + ",-");
        }
    }

    public void generate(NullStatement AST) {
        output.accept(AST.getLine() + ": generating code for NullStatement\n");
        AST.setCode("");
    }


    public void generate(BranchStatement AST) {
        output.accept(AST.getLine() + ": generating code for BranchStatement\n");
        //TODO
        String endBranchLabel = getNewLabel();
        String temp = generate(AST.getAddexp());

        generate(endBranchLabel, getNewLabel(), AST.getCaseStmt());

        AST.appendCode("lab " + endBranchLabel);
    }


    public void generate(String endBranchLabel, String thisCasesStartLabel, CaseStatement statement) {

        String nextLabel = getNewLabel();
        int NUM = statement.getNumberToken().getAttrValue();
        statement.setCode("iff " + NUM + ",-," + nextLabel);
        generate(statement.getStatement()); // statement for this case
        statement.appendCode(statement.getStatement().getCode());
        statement.appendCode("goto " + nextLabel);

        generate((CaseStatement) statement.getNextNode()); // next case in branch statement
        statement.appendCode(statement.getStatement().getCode());
    }

    public String generate(Expression AST) {
        output.accept(AST.getLine() + ": generating code for Expression\n");

        String temp = generate(AST.getAddExp());
        AST.setCode(AST.getAddExp().getCode());

        if (AST.getAddExp2() != null) {
            generate(AST.getAddExp2());
            AST.appendCode(AST.getAddExp2().getCode());
        }
        return temp;
    }

    public String generate(AddExpression AST) {
        output.accept(AST.getLine() + ": generating code for AddExpression\n");

        String newLabel = getNewLabel();

        String temp = generate(AST.getTerm());
        AST.setCode(AST.getTerm().getCode());
        AST.setTemp(temp);

        AddOpTerm next = AST.getNextNode();
        if (next != null) {
            String temp2 = generate(next);

            String newTemp = getNewTemp();

            switch (next.getAddOp()) {
                case PLUS:
                    AST.appendCode(next.getCode());
                    AST.appendCode("add " + temp + " " + temp2 + " " + newTemp);
                    break;
                case MINUS:
                    AST.appendCode(next.getCode());
                    AST.appendCode("sub " + temp + " " + temp2 + " " + newTemp);
                    break;
                case OR:
                    AST.appendCode(next.getCode());
                    AST.appendCode("or " + temp + " " + temp2 + " " + newTemp);
                    break;
                case ORELSE:
                    AST.appendCode("ift " + temp + " " + newLabel);
                    AST.appendCode(next.getCode());
                    AST.appendCode("or " + temp + " " + temp2 + " " + newTemp);
                    break;
            }
            AST.appendCode("asg " + newTemp + " " + temp);
        }

        AST.appendCode("lab " + newLabel);

        return temp;
    }

    public String generate(Term AST) {
        output.accept(AST.getLine() + ": generating code for Term\n");

        String temp = generate(AST.getFactor());
        AST.setCode(AST.getFactor().getCode());

        MultOpFactor next = (MultOpFactor) AST.getFactor().getNextNode();
        if (next != null) {
            String temp2 = generate(next);
            AST.appendCode(next.getCode());


        }

        return temp;
    }

    public String generate(SubExpression AST) {
        output.accept(AST.getLine() + ": generating code for SubExpression\n"); //Nothing to analyze here

        if (AST instanceof IdFactor)
            return generate((IdFactor) AST);

        else if (AST instanceof LiteralBool)
            return generate((LiteralBool) AST);

        else if (AST instanceof LiteralNum)
            return generate((LiteralNum) AST);

        else if (AST instanceof AddOpTerm) {
            String temp = generate(((AddOpTerm) AST).getTerm());
            AST.setCode(((AddOpTerm) AST).getTerm() + "");
            return temp;
        } else if (AST instanceof MinusExpression) {

        } else if (AST instanceof MultOpFactor)
            return generate((MultOpFactor) AST);

        else if (AST instanceof NotNidFactor)
            return generate((NotNidFactor) AST);

        else if (AST instanceof Term)
            return generate((Term) AST);

        else if (AST instanceof AddExpression)
            return generate((AddExpression) AST);

        else if (AST instanceof Expression)
            return generate((Expression) AST);

        throw new WTFException("Didn't find method to go to for ASTNode: " + AST);
    }

    public String generate(LiteralBool AST) {
        output.accept(AST.getLine() + ": generating code for LiteralBool\n"); //Nothing to analyze here
        String temp = getNewTemp();
        AST.setCode("asg " + AST.getValue() + ",-," + temp);
        return temp;
    }

    public String generate(LiteralNum AST) {
        output.accept(AST.getLine() + ": generating code for LiteralNum\n"); //Nothing to analyze here
        String temp = getNewTemp();
        AST.setCode("asg " + AST.getNum() + ",-," + temp);
        return temp;
    }

    public String generate(MultOpFactor AST) {
        output.accept(AST.getLine() + ": generating code for MultOpFactor\n");
        String temp = generate(AST.getFactor());
        AST.setCode(AST.getFactor().getCode());
        return temp;
    }

    public String generate(NotNidFactor AST) {
        output.accept(AST.getLine() + ": generating code for NotNidFactor\n");

        String temp = generate(AST.getFactor());
        AST.setCode(AST.getFactor().getCode());
        String newTemp = getNewTemp();
        AST.appendCode("not " + temp + ",-," + newTemp);
        return newTemp;
    }

    public String generate(IdFactor AST) {
        output.accept(AST.getLine() + ": generating code for IdFactor\n");
        String newTemp = getNewTemp();
        String temp;
        if (AST.getIdTail() instanceof CallStatementTail) {
            temp = generate((CallStatementTail) AST.getIdTail());
            //TODO maybe something here
            AST.setCode(AST.getIdTail().getCode());
            AST.appendCode("asg " + temp + ",-," + newTemp);
        } else if (AST.getIdTail() instanceof AddExpression) {
            temp = generate((AddExpression) AST.getIdTail());
            AST.setCode(AST.getIdTail().getCode());
            AST.appendCode("fae " + AST.getDecl().getID().getName() + "," + temp + "," + newTemp);
        } else
            throw new WTFException("Has to be either a call stmt tail or add expression");

        return newTemp;
    }


    public void generate(ASTNode AST) {
        //TODO do something with this
        output.accept(AST.getLine() + ": generating code for ASTNode\n");
    }

    public String getNewTemp() {
        return "t" + tempCounter++;
    }

    public String getNewLabel() {
        return "L" + labelCounter++;
    }

}
