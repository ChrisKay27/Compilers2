package parser.grammar.expressions;

import parser.TokenType;
import parser.grammar.ASTNode;

/**
 * Created by Chris on 2/12/2016.
 */
public class Term extends Subexpression {
    private final Subexpression factor;
    private final TokenType multop;
    private final Subexpression factor2;

    public Term(Subexpression factor, TokenType multop, Subexpression factor2) {
        this.factor = factor;
        this.multop = multop;
        this.factor2 = factor2;
    }
    public Term(Subexpression factor) {
        this(factor, null, null);
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName());

        factor.appendContents(sb, tabs + 1);
        if( multop != null) {
            sb.append('\n').append(getTabs(tabs)).append(multop);
            factor2.appendContents(sb, tabs + 1);
        }

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
