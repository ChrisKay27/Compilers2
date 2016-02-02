package parser.grammar;

import parser.Tokens;

/**
 * Created by Chris on 1/30/2016.
 */
public class Expression extends AST {
    private final AddExpression addExpression;
    private final Tokens relop;
    private final AddExpression addExp2;


    public Expression(AddExpression addExpression, Tokens relop, AddExpression addExp2) {
        this.addExpression = addExpression;
        this.relop = relop;
        this.addExp2 = addExp2;
    }

    public AddExpression getAddExpression() {
        return addExpression;
    }

    public Tokens getRelop() {
        return relop;
    }

    public AddExpression getAddExp2() {
        return addExp2;
    }
}
