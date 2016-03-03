package parser.grammar.statements;


import parser.grammar.expressions.Expression;
import parser.Type;

/**
 * Created by Chris on 1/30/2016.
 */
public class ReturnStatement extends Statement {
    private final Expression returnValue;

    public ReturnStatement(String line, Expression returnValue) {
        super(line);
        this.returnValue = returnValue;
    }

    public Expression getReturnValue() {
        return returnValue;
    }

    public Type getType() {
        return returnValue.getType();
    }
}
