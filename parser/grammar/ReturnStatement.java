package parser.grammar;

import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 1/30/2016.
 */
public class ReturnStatement extends Statement {
    private final Expression returnValue;

    public ReturnStatement(Expression returnValue) {
        super();
        this.returnValue = returnValue;
    }

    public Expression getReturnValue() {
        return returnValue;
    }
}
