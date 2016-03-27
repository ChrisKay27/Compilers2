package parser.grammar.statements;

import parser.grammar.declarations.Declaration;
import parser.grammar.expressions.AddExpression;
import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 2/12/2016.
 */
public class AssignStatementTail extends StatementTail {

    private final AddExpression addExpression;
    private final Expression exp;

    private Declaration decl;

    public AssignStatementTail(int line, AddExpression addExpression, Expression exp) {
        super(line);
        this.addExpression = addExpression;
        this.exp = exp;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
//        sb.append(' ');

        if (addExpression != null)
            addExpression.appendContents(sb, tabs + 1);
        exp.appendContents(sb, tabs + 1);
    }

    public AddExpression getAddExpression() {
        return addExpression;
    }

    public Expression getExp() {
        return exp;
    }

    public Declaration getDecl() {
        return decl;
    }

    public void setDecl(Declaration decl) {
        this.decl = decl;
    }
}
