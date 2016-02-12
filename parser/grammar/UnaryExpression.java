package parser.grammar;

import parser.TokenType;

/**
 * Created by Chris on 2/12/2016.
 */
public class UnaryExpression extends Expression {
    public UnaryExpression(AddExpression addExpression, TokenType relop, AddExpression addExp2) {
        super(addExpression, relop, addExp2);
    }
}
