package parser.grammar.statements;


import parser.Type;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 1/30/2016.
 */
public class ReturnStatement extends Statement {

    private final Expression returnValue;
    private FuncDeclaration funcDecl;

    public ReturnStatement(int line, Expression returnValue) {
        super(line);
        this.returnValue = returnValue;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());

        sb.append(tabsStr).append("\treturn value:");
        returnValue.appendContents(sb,tabs+2);

        if (nextNode != null)
            System.err.println("Warning: Unreachable code found after return statement on line " + getLine() + ".");
    }

    public Expression getReturnValue() {
        return returnValue;
    }

    public Type getType() {
        return returnValue.getType();
    }

    public FuncDeclaration getFuncDecl() {
        return funcDecl;
    }

    public void setFuncDecl(FuncDeclaration funcDecl) {
        this.funcDecl = funcDecl;
    }


}
