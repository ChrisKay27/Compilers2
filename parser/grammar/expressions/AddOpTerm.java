package parser.grammar.expressions;

import parser.TokenType;
import parser.grammar.ASTNode;

/**
 * Created by Carston on 2/21/2016.
 */
public class AddOpTerm extends ASTNode {

    private final TokenType addOp;

    private final Term term;

    public AddOpTerm(TokenType addOp, Term term) {
        this.addOp = addOp;
        this.term = term;
    }

    public TokenType getAddOp() { return addOp; }

    public Term getTerm() { return term; }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {

        sb.append('\n');
        String tabsStr = getTabs(tabs);
        sb.append(tabsStr);
        sb.append(getClass().getSimpleName());

        sb.append(" " + addOp + " ");
        term.appendContents(sb, tabs + 1);
        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
