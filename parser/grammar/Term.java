package parser.grammar;

import parser.TokenType;

/**
 * Created by Chris on 2/12/2016.
 */
public class Term extends ASTNode {
    private final ASTNode factor;
    private final TokenType multop;
    private final ASTNode factor2;

    public Term(ASTNode factor, TokenType multop, ASTNode factor2) {
        this.factor = factor;
        this.multop = multop;
        this.factor2 = factor2;
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
