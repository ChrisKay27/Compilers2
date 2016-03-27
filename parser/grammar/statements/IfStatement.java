package parser.grammar.statements;


import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 1/30/2016.
 */
public class IfStatement extends Statement {

    private final Expression expression;
    private final Statement statement;
    private final Statement elseStatement;

    public IfStatement(int line, Expression expression, Statement statement, Statement elseStatement) {
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
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());

        sb.append(tabsStr).append("expression: ");
        expression.appendContents(sb, tabs + 2);

        sb.append(tabsStr).append("statement: ");
        statement.appendContents(sb,tabs + 2);

        if( elseStatement != null ) {
            sb.append(tabsStr).append("else statement: ");
            elseStatement.appendContents(sb, tabs + 2);
        }
        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
