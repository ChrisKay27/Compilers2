package parser.grammar.expressions;

import parser.TokenType;
import parser.grammar.ASTNode;

/**
 * Created by Chris on 1/30/2016.
 */
public class AddExpression extends ASTNode {
    private final boolean uminus;
    private final Term term;

    private final TokenType addop;
    private final Term term2;

    public AddExpression(boolean uminus, Term term, TokenType addop, Term term2) {
        this.uminus = uminus;
        this.term = term;
        this.addop = addop;
        this.term2 = term2;
    }

    public AddExpression(boolean minusExpr, Term term) {this(minusExpr, term, null, null);}

    public boolean isUminus() {
        return uminus;
    }

    public Term getTerm() {
        return term;
    }

    public TokenType getAddop() {
        return addop;
    }

    public Term getTerm2() {
        return term2;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        String tabsStr = getTabs(tabs);
        sb.append(tabsStr);
        sb.append(getClass().getSimpleName());

        if(uminus)
            sb.append(" - ");

        term.appendContents(sb, tabs + 1);

        if( addop != null) {
            sb.append('\n').append(tabsStr);
            sb.append(addop);
            term2.appendContents(sb, tabs + 1);
        }

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
