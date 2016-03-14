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
