package parser.grammar.expressions;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 2/12/2016.
 */
public class NotNidFactor extends NidFactor {

    private final ASTNode factor;

    public NotNidFactor(ASTNode factor) {
        this.factor = factor;
    }
    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        sb.append(getTabs(tabs));
        sb.append(getClass().getSimpleName());
        factor.appendContents(sb, tabs + 1);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs + 2);
    }
}
