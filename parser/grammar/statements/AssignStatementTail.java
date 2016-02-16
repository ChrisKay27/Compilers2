package parser.grammar.statements;

import parser.grammar.expressions.AddExpression;
import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 2/12/2016.
 */
public class AssignStatementTail extends StatementTail {
    private final AddExpression addExpression;
    private final Expression exp;

    public AssignStatementTail(AddExpression addExpression, Expression exp) {

        this.addExpression = addExpression;
        this.exp = exp;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append('\t');
        sb.append(getClass().getSimpleName());
        sb.append(' ');

        addExpression.appendContents(sb, tabs);
        exp.appendContents(sb,tabs);
    }
}
