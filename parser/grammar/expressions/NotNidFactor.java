package parser.grammar.expressions;

/**
 * Created by Chris on 2/12/2016.
 */
public class NotNidFactor extends NidFactor {

    private final SubExpression factor;

    public NotNidFactor(SubExpression factor) {
        this.factor = factor;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        sb.append(getTabs(tabs));
        sb.append(getClass().getSimpleName() + " Line: " + getLine());
        factor.appendContents(sb, tabs + 1);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs + 2);
    }

    public SubExpression getFactor() {
        return factor;
    }
}
