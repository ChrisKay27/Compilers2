package parser.grammar.statements;


import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 1/30/2016.
 */
public class IfStatement extends Statement {

    private final Expression expression;
    private final Statement statement;
    private final Statement elseStatement;

    public IfStatement(Expression expression, Statement statement, Statement elseStatement) {
        this.expression = expression;
        this.statement = statement;
        this.elseStatement = elseStatement;
    }

    public Expression getExpression() {
        return expression;
    }

    public Statement getStatement() {
        return statement;
    }

    public Statement getElseStatement() {
        return elseStatement;
    }
}
