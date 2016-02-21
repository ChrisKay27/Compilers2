package parser.grammar.expressions;

import parser.TokenType;
import parser.grammar.ASTNode;

/**
 * Created by Carston on 2/21/2016.
 */
public class MultOpFactor extends ASTNode {

    private final TokenType addOp;
    private final Factor factor;

    public MultOpFactor(TokenType addOp, Factor term) {
        this.addOp = addOp;
        this.factor = term;
    }

    public TokenType getMultOp() { return addOp; }

    public Factor getFactor() { return factor; }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {

        sb.append('\n');
        String tabsStr = getTabs(tabs);
        sb.append(tabsStr);
        sb.append(getClass().getSimpleName());

        sb.append(" " + addOp + " ");
        factor.appendContents(sb, tabs + 1);
        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
