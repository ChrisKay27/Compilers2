package parser.grammar.expressions;

import parser.TokenType;
import parser.grammar.expressions.AddExpression;
import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 2/12/2016.
 */
public class UnaryExpression extends Expression {
    public UnaryExpression(AddExpression addExpression, TokenType relop, AddExpression addExp2) {
        super(addExpression, relop, addExp2);
    }
}
