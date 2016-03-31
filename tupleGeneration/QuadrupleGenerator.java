package tupleGeneration;

//import com.sun.org.apache.xpath.internal.operations.Mult;
import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.declarations.ParamDeclaration;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.*;
import parser.grammar.statements.*;
import util.WTFException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class generates quadruple code in symbolic form.
 * Created by Carston on 2/27/2016.
 */
public class QuadrupleGenerator {

    private final Consumer<String> output;
    private final Consumer<String> regError;
    private ASTNode astRoot;
    private BiConsumer<Integer, String> error;
    private boolean foundError;

    private FuncDeclaration currentFuncDecl;

    private int labelCounter;
    private int tempCounter;
    private Stack<String> currentLoopStartLabels = new Stack<>();
    private Stack<String> currentLoopEndLabels = new Stack<>();
    private Stack<Integer> levelStack = new Stack<>();
    private Stack<Integer> tempCountStack = new Stack<>();
    private boolean executableQuadruple;


    public QuadrupleGenerator(ASTNode astRoot, Consumer<String> output, Consumer<String> error, BiConsumer<Integer, String> lineError, boolean executableQuadruple) {
        this.astRoot = astRoot;
        this.output = output;
        this.regError = error;
        this.error = lineError;
        this.executableQuadruple = executableQuadruple;
    }


    public String startCodeGeneration(Declaration AST) {
        levelStack.push(0);

        FuncDeclaration lastFunction = null;

        //Count global variables
        int numGlobalVars = 0;
        Declaration current = AST;
        do {
            current.setLevel(0);
            if (current instanceof VarDeclaration)
                numGlobalVars++;
            //TODO do something with this
            current = current.getNextNode();
        }
        while (current != null);

        current = AST;
        do {
            if (!(current instanceof VarDeclaration))
                generate((FuncDeclaration) current);

            if (AST != current)
                AST.appendCode(current.getCode());
            current = current.getNextNode();
        }
        while (current != null);

        tempCountStack.push(0);// 0 was previously numGlobalVars

        AST.setCode(
                "(start," + numGlobalVars + ",-,-)\n" +
                        "(rval,-,-," + getNewTemp() + ")\n" +
                        "(call,main,-,-)\n" +
                        "(hlt,-,-,-)\n"
                        + AST.getCode());

//        output.accept("\n" + AST.getCode());

        return AST.getCode();
    }

    public void generate(FuncDeclaration AST) {
        output.accept(AST.getLine() + ": generating code for FuncDeclaration\n");

        enteringFuncDeclaration(AST);
        AST.setCode("(fun," + AST.getID().name + "," + AST.getNumberOfLocals() + ",-)");
//        generate(AST.getParams());

        generate(AST.getBody(), true);
        AST.appendCode(AST.getBody().getCode());


        leavingFuncDeclaration();
    }

    private void enteringFuncDeclaration(FuncDeclaration AST) {
//        output.accept("  enteringFuncDeclaration\n");
        currentFuncDecl = AST;
        levelStack.push(1);
        tempCountStack.push(1 + currentFuncDecl.getNumberOfLocals());
    }

    private void enteringCompoundStmt(int locals) {
        levelStack.push(levelStack.peek() + 1);
        tempCountStack.push(locals);
    }

    private void leavingCompoundStmt() {
        levelStack.pop();
        tempCountStack.pop();
    }

//    // I don't think we need to generate any code for param declarations
//    public void generate(ParamDeclaration AST) {
//        output.accept(AST.getLine() + ": generating code for ParamDeclaration\n");
//        while (AST != null) {
//            //TODO do something with this param dec
//            AST = (ParamDeclaration) AST.getNextNode();
//        }
//    }

    private void leavingFuncDeclaration() {
//        output.accept("  leavingFuncDeclaration\n");
        tempCountStack.pop();
        levelStack.pop();
        currentFuncDecl = null;
    }


    public void generate(Statement AST) {
        output.accept(AST.getLine() + ": generating code for Statement\n");

        if (AST instanceof IdStatement)
            generate((IdStatement) AST);
        else if (AST instanceof IfStatement)
            generate((IfStatement) AST);
        else if (AST instanceof CompoundStatement)
            generate((CompoundStatement) AST, false);
        else if (AST instanceof LoopStatement)
            generate((LoopStatement) AST);
        else if (AST instanceof ContinueStatement)
            generate((ContinueStatement) AST);
        else if (AST instanceof ExitStatement)
            generate((ExitStatement) AST);
        else if (AST instanceof ReturnStatement)
            generate((ReturnStatement) AST);
        else if (AST instanceof BranchStatement)
            generate((BranchStatement) AST);
        else if (AST instanceof NullStatement)
            generate((NullStatement) AST);
    }

    public void generate(IdStatement AST) {
        output.accept(AST.getLine() + ": generating code for IdStatement\n");

        String assignedValue = generate(AST.getIdToken().name, AST.getId_stmt_tail());
        AST.appendCode(AST.getId_stmt_tail().getCode());

        //assigned value will be null IF this is an assignment into an array OR a function call
        if (assignedValue != null)
            AST.appendCode("(asg," + assignedValue + ",-," + AST.getDecl().getLD() + ')');
    }

    public String generate(String name, StatementTail AST) {
        output.accept(AST.getLine() + ": generating code for StatementTail\n");

        if (AST instanceof AssignStatementTail)
            return generate(name, (AssignStatementTail) AST);
        else if (AST instanceof CallStatementTail) {
            return generate((CallStatementTail) AST);
        }
        throw new WTFException("Malformed statement tail found in quadruple generation stage");
    }

    /**
     * @param name name of the id being assigned to
     * @return null IF this is an array assignment ELSE will return the temp of the result of the expression
     */
    public String generate(String name, AssignStatementTail AST) {
        output.accept(AST.getLine() + ": generating code for AssignStatementTail\n");

        String returnedValue = generate(AST.getExp());

        AST.appendCode(AST.getExp().getCode());

        //TODO FIX THIS ARRAY INDEX DOESN'T WORK, it might now...
        String temp;
        //If its an array index
        if (AST.getAddExpression() != null) {
            temp = generate(AST.getAddExpression());
            AST.appendCode(AST.getAddExpression().getCode());
            AST.appendCode("(tae," + returnedValue + "," + temp + "," + name + ")");
            return null;
        }

        return returnedValue;
    }

    public void generate(IfStatement AST) {
        output.accept(AST.getLine() + ": generating code for IfStatement\n");

        //generate code for the expression
        String returnedValue = generate(AST.getExpression());

        Statement elseStatement = AST.getElseStatement();
        boolean hasElse = elseStatement != null;
        String elseLabel = getNewLabel();
        String exitLabel = (hasElse ? getNewLabel() : null);

        //Set the code for expression
        AST.setCode(AST.getExpression().getCode());

        AST.appendCode("(iff," + returnedValue + ",-," + elseLabel + ")");
        generate(AST.getStatement());
        AST.appendCode(AST.getStatement().getCode());
        if (hasElse) AST.appendCode("(goto,-,-," + exitLabel + ")");
        AST.appendCode("(lab,-,-," + elseLabel + ")");

        if (hasElse) {
            generate(elseStatement);
            AST.appendCode(elseStatement.getCode());
            AST.appendCode("(lab,-,-," + exitLabel + ")");
        }
    }

    /**
     * @return null IF the the function does not return anything
     */
    public String generate(CallStatementTail AST) {
        output.accept(AST.getLine() + ": generating code for CallStatementTail\n");

        {// handle case where the function call is a library function
            String functionId = AST.getFuncDecl().getID().getName();
            String targetRegister = "";
            Expression exp;
            switch (functionId) {
                case "readint":
                    targetRegister = getNewTemp();
                    AST.appendCode("(rdi,-,-," + targetRegister + ")");
                    return targetRegister;
                case "readbool":
                    targetRegister = getNewTemp();
                    AST.appendCode("(rdb,-,-," + targetRegister + ")");
                    return targetRegister;
                case "writeint":
                    exp = (Expression) AST.getCall_tail();
                    targetRegister = generate(exp);
                    AST.appendCode(exp.getCode());
                    AST.appendCode("(wri," + targetRegister + ",-,-)");
                    return null;
                case "writebool":
                    exp = (Expression) AST.getCall_tail();
                    targetRegister = generate(exp);
                    AST.appendCode(exp.getCode());
                    AST.appendCode("(wrb," + targetRegister + ",-,-)");
                    return null;
                default:
                    break;
            }
        }

        String returnValTemp = "";
        if (AST.getFuncDecl().hasReturnValue()) {
            returnValTemp = getNewTemp();
            AST.appendCode("(rval,-,-," + returnValTemp + ")");
        }

        if (AST.getCall_tail() != null) {
            Expression current = (Expression) AST.getCall_tail();

            ParamDeclaration pdec = AST.getFuncDecl().getParams();
            Stack<String> arguments = new Stack<String>(); // stack for argument code, push to it then pop off and append code before calling function
            while (current != null) {

                String temp2 = generate(current);
                AST.appendCode(current.getCode());

                if (pdec.isReference())
                    AST.appendCode("(arga," + temp2 + ",-,-)");
                else
                    AST.appendCode("(arg," + temp2 + ",-,-)");

                if (current.getNextNode() instanceof Expression) {
                    current = (Expression) current.getNextNode();
                    pdec = (ParamDeclaration) pdec.getNextNode();
                } else {
                    current = null;
                }
            }
        }

        AST.appendCode("(call," + AST.getFuncDecl().getID().getName() + ",-,-)");

        if (AST.getFuncDecl().hasReturnValue())
            return returnValTemp;
        return null;
    }

    public void generate(CompoundStatement AST, boolean funcBody) {
        output.accept(AST.getLine() + ": generating code for CompoundStatement\n");

        Declaration d = AST.getDeclarations();
        if (d != null && !funcBody) {

            AST.appendCode("(ecs," + d.getLength() + ",-,-)");
            enteringCompoundStmt(d.getLength());

        }

        Statement stmt = AST.getStatements();

        while (stmt != null) {

            Statement nextStmt = (Statement) stmt.getNextNode();
            //if (nextStmt == null && d != null)
            //    AST.appendCode("(lcs,-,-,-)");

            generate(stmt);
            AST.appendCode(stmt.getCode());
            stmt = nextStmt;
        }

        if (d != null && !funcBody) {
            leavingCompoundStmt();
            AST.appendCode("(lcs,-,-,-)");
        }
//        if (d != null)
//            AST.appendCode("(lcs,-,-,-)");
    }

    public void generate(LoopStatement AST) {
        output.accept(AST.getLine() + ": generating code for LoopStatement\n");

        currentLoopStartLabels.push(getNewLabel());
        currentLoopEndLabels.push(getNewLabel());

        Statement statement = AST.getStatement();
        generate(statement);
        AST.appendCode("(lab,-,-," + currentLoopStartLabels.peek() + ")");
        AST.appendCode(statement.getCode());
        AST.appendCode("(lab,-,-," + currentLoopEndLabels.peek() + ")");

        currentLoopStartLabels.pop();
        currentLoopEndLabels.pop();
    }

    public void generate(ExitStatement AST) {
        output.accept(AST.getLine() + ": generating code for ExitStatement\n");
        AST.appendCode("(goto,-,-," + currentLoopEndLabels.peek() + ")");
    }


    public void generate(ContinueStatement AST) {
        output.accept(AST.getLine() + ": generating code for ContinueStatement\n");
        AST.appendCode("(goto,-,-," + currentLoopStartLabels.peek() + ")");
    }

    public void generate(ReturnStatement AST) {
        output.accept(AST.getLine() + ": generating code for ReturnStatement\n");
        String temp = generate(AST.getReturnValue());
        if (AST.getReturnValue() == null) {
            AST.setCode("(ret," + currentFuncDecl.getNumberOfParameters() + ",-,-)");
        } else {
            AST.setCode(AST.getReturnValue().getCode());
            AST.appendCode("(retv," + currentFuncDecl.getNumberOfParameters() + "," + temp + ",-)");
        }
    }

    public void generate(NullStatement AST) {
        output.accept(AST.getLine() + ": generating code for NullStatement\n");
//        AST.setCode("(");
    }


    public void generate(BranchStatement AST) {
        output.accept(AST.getLine() + ": generating code for BranchStatement\n");
        //TODO
        String endBranchLabel = getNewLabel();
        String temp = generate(AST.getAddexp());

        generate(endBranchLabel, getNewLabel(), temp, AST.getCaseStmt());

        AST.appendCode("(lab,-,-," + endBranchLabel + ")");
    }


    public void generate(String endBranchLabel, String thisCasesStartLabel, String branchConditionTemp, CaseStatement statement) {
        //Create label for next case statement
        String nextLabel = getNewLabel();
        statement.appendCode("(lab,-,-," + thisCasesStartLabel + ")");

        //Check to see if the
        int NUM = statement.getNumberToken().getID();
        statement.setCode("(iff," + NUM + ",-," + nextLabel + ")");

        String temp = getNewTemp();
        statement.setCode("(eq," + NUM + "," + branchConditionTemp + "," + temp + ")");
        statement.setCode("(iff," + NUM + "," + temp + "," + nextLabel + ")");
        generate(statement.getStatement()); // statement for this case
        statement.appendCode(statement.getStatement().getCode());
        statement.appendCode("(goto,-,-,L" + endBranchLabel + ")");

        if (statement.getNextNode() != null) {
            generate(endBranchLabel, nextLabel, branchConditionTemp, (CaseStatement) statement.getNextNode()); // next case in branch statement
            statement.appendCode(statement.getNextNode().getCode());
        }
    }

    public String generate(Expression AST) {
        output.accept(AST.getLine() + ": generating code for Expression\n");

        String temp = generate(AST.getAddExp());

        AST.setCode(AST.getAddExp().getCode());


        if (AST.getAddExp2() != null) {
            String temp2 = generate(AST.getAddExp2());
            AST.appendCode(AST.getAddExp2().getCode());

            String newTemp = getNewTemp();
            switch (AST.getRelop()) {
                case LT:
                    AST.appendCode("(lt," + temp + "," + temp2 + "," + newTemp + ")");
                    break;
                case LTEQ:
                    AST.appendCode("(lte," + temp + "," + temp2 + "," + newTemp + ")");
                    break;
                case GT:
                    AST.appendCode("(gt," + temp + "," + temp2 + "," + newTemp + ")");
                    break;
                case GTEQ:
                    AST.appendCode("(gte," + temp + "," + temp2 + "," + newTemp + ")");
                    break;
                case EQ:
                    AST.appendCode("(eq," + temp + "," + temp2 + "," + newTemp + ")");
                    break;
                case NEQ:
                    AST.appendCode("(neq," + temp + "," + temp2 + "," + newTemp + ")");
                    break;
                default:
                    throw new WTFException("Error! bad token type in Expression relop");
            }
            AST.appendCode("(asg," + newTemp + ",-," + temp + ")");
        }

        return temp;
    }

    public String generate(AddExpression AST) {
        output.accept(AST.getLine() + ": generating code for AddExpression\n");

        String argument1 = generate(AST.getTerm());
        AST.setCode(AST.getTerm().getCode());

        if (AST.isUminus()) {
            String unaryMinusTemp = getNewTemp();
            AST.appendCode("(mul," + argument1 + ",-1," + unaryMinusTemp + ")");
            AST.appendCode("(asg," + unaryMinusTemp + ",-," + argument1 + ")");
        }

        //AST.setTemp(argument1);
        String resultTemp = null;
        AddOpTerm next = AST.getNextNode();
        while (next != null) {
            String argument2 = generate(next);
            resultTemp = getNewTemp();
            String newLabel = null;
            switch (next.getAddOp()) {
                case PLUS:
                    AST.appendCode(next.getCode());
                    AST.appendCode("(add," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
                case MINUS:
                    AST.appendCode(next.getCode());
                    AST.appendCode("(sub," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
                case OR:
                    AST.appendCode(next.getCode());
                    AST.appendCode("(or," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
                case ORELSE:
                    newLabel = getNewLabel();
                    AST.appendCode("(ift," + argument1 + ",-," + newLabel + ")");
                    AST.appendCode(next.getCode());
                    AST.appendCode("(or," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
                default:
                    throw new WTFException("Error! bad token type in AddExpression addop");
            }
            AST.appendCode("(asg," + resultTemp + ",-," + argument1 + ")");

            next = (AddOpTerm) next.getNextNode();

            if (newLabel != null)
                AST.appendCode("(lab,-,-," + newLabel + ")");
        }

        if (resultTemp != null)
            return resultTemp;
        else return argument1;
    }

    public String generate(Term AST) {
        output.accept(AST.getLine() + ": generating code for Term\n");
        String newLabel = null;
        String argument1 = generate(AST.getFactor());

        AST.setCode(AST.getFactor().getCode());

        MultOpFactor next = AST.getNextNode();
        String resultTemp = null;
        while (next != null) {
            String argument2 = generate(next);
            resultTemp = this.getNewTemp();
            switch (next.getMultOp()) {
                case MULT:
                    AST.appendCode(next.getCode());
                    AST.appendCode("(mul," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
                case DIV:
                    AST.appendCode(next.getCode());
                    AST.appendCode("(div," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
                case MOD:
                    AST.appendCode(next.getCode());
                    AST.appendCode("(mod," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
                case ANDTHEN: // TODO i think and isn't short circuited, possibly broken
                    newLabel = getNewLabel();
                    AST.appendCode("(iff," + argument1 + ",-," + newLabel + ")");
                    AST.appendCode(next.getCode());
                    AST.appendCode("(and," + argument1 + "," + argument2 + "," + resultTemp + ")");
                    break;
            }
            argument1 = resultTemp;
//
//            AST.appendCode(next.getCode());
            next = (MultOpFactor) next.getNextNode();

            if (newLabel != null)
                AST.appendCode("(lab,-,-," + newLabel + ")");
        }
        if (resultTemp != null)
            return resultTemp;
        else return argument1;
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
            AST.setCode(((AddOpTerm) AST).getTerm().getCode());
            return temp;
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
        AST.setCode("(asg," + AST.getValue() + ",-," + temp + ")");
        return temp;
    }


    public String generate(LiteralNum AST) {
        output.accept(AST.getLine() + ": generating code for LiteralNum\n"); //Nothing to analyze here
        String temp = getNewTemp();
        AST.setCode("(asg," + AST.getNum() + ",-," + temp + ")");
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
        AST.appendCode("(not," + temp + ",-," + newTemp + ")");
        return newTemp;
    }

    public String generate(IdFactor AST) {
        output.accept(AST.getLine() + ": generating code for IdFactor\n");
        String newTemp = getNewTemp();
        String temp;

        //If this is an id factor which is a function call then its arguments will be the expressions in the id tail.
        if (AST.getIdTail() instanceof CallStatementTail) {
            CallStatementTail cst = (CallStatementTail) AST.getIdTail();
            Expression current = (Expression) cst.getCall_tail();
            FuncDeclaration funcDecl = (FuncDeclaration) AST.getDecl();
            ParamDeclaration pdec = funcDecl.getParams();

            //Keep track of the arguments for when we actually call the function.
            List<String> arguments = new ArrayList<>();

            //Evaluate all the arguments
            while (current != null) {
                String temp2 = generate(current);
                AST.appendCode(current.getCode());

                if (pdec.isReference())
                    arguments.add("(arga," + temp2 + ",-,-)");
                else
                    arguments.add("(arg," + temp2 + ",-,-)");

                if (current.getNextNode() instanceof Expression) {
                    current = (Expression) current.getNextNode();
                    pdec = (ParamDeclaration) pdec.getNextNode();
                } else {
                    current = null;
                }
            }

            arguments.forEach(AST::appendCode);
            AST.appendCode("(rval,-,-," + newTemp + ")");
            AST.appendCode("(call," + AST.getIdToken().getName() + ",-,-)");

        } else if (AST.getIdTail() instanceof AddExpression) {
            temp = generate((AddExpression) AST.getIdTail());
            AST.setCode(AST.getIdTail().getCode());
            AST.appendCode("(fae," + AST.getDecl().getLD() + "," + temp + "," + newTemp + ")");

        } else
            AST.appendCode("(asg," + AST.getDecl().getLD() + ",-," + newTemp + ")");

        return newTemp;
    }


    public void generate(ASTNode AST) {
        //TODO do something with this
        output.accept(AST.getLine() + ": generating code for ASTNode\n");
    }

    public String getNewTemp() {
        if (executableQuadruple) {
            tempCountStack.push(tempCountStack.pop() + 1);
            return "(" + levelStack.peek() + "," + tempCountStack.peek() + ")";
        } else
            return "t" + tempCounter++;
    }

    public String getNewLabel() {
        return "L" + labelCounter++;
    }

}
