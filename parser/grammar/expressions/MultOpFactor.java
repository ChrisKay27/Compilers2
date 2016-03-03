package parser.grammar.expressions;

import parser.TokenType;

/**
 * Created by Carston on 2/21/2016.
 */
public class MultOpFactor extends SubExpression {

    private final TokenType addOp;
    private final SubExpression factor;

    public MultOpFactor(String line, TokenType addOp, SubExpression term) {
        super(line);
        this.addOp = addOp;
        this.factor = term;
    }

    public TokenType getMultOp() { return addOp; }

    public SubExpression getFactor() { return factor; }

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
