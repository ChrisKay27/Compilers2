package parser.grammar;

import parser.TokenType;

/**
 * Created by Chris on 2/12/2016.
 */
public class BinaryExpression extends Expression {


    public BinaryExpression(AddExpression addExpression, TokenType relop, AddExpression addExp2) {
        super(addExpression, relop, addExp2);
    }
}
