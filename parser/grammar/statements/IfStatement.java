package parser.grammar.statements;


import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 1/30/2016.
 */
public class IfStatement extends Statement {

    private final Expression expression;
    private final Statement statement;
    private final Statement elseStatement;

    public IfStatement(String line, Expression expression, Statement statement, Statement elseStatement) {
        super(line);
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

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName());

        expression.appendContents(sb, tabs + 1);
        statement.appendContents(sb,tabs+1);

        if( elseStatement != null )
            elseStatement.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
