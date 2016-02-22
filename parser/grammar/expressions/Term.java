package parser.grammar.expressions;

/**
 * Created by Chris on 2/12/2016.
 */
public class Term extends Subexpression {

    private final Subexpression factor;

    public Term(Subexpression factor) {
        this.factor = factor;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName());

        factor.appendContents(sb, tabs + 1);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
