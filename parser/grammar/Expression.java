package parser.grammar;

import parser.TokenType;

/**
 * Created by Chris on 1/30/2016.
 */
public class Expression extends AST {
    private final AddExpression addExpression;
    private final TokenType relop;
    private final AddExpression addExp2;


    public Expression(AddExpression addExpression, TokenType relop, AddExpression addExp2) {
        this.addExpression = addExpression;
        this.relop = relop;
        this.addExp2 = addExp2;
    }

    public AddExpression getAddExpression() {
        return addExpression;
    }

    public TokenType getRelop() {
        return relop;
    }

    public AddExpression getAddExp2() {
        return addExp2;
    }
}
