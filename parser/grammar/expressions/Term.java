package parser.grammar.expressions;

/**
 * Created by Chris on 2/12/2016.
 */
public class Term extends SubExpression {

    private final SubExpression factor;

    public Term(int line, SubExpression factor) {
        super(line);
        this.factor = factor;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getLine() + ":" + getClass().getSimpleName());

        factor.appendContents(sb, tabs + 1);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }

    public SubExpression getFactor() {
        return factor;
    }

    public MultOpFactor getNextNode(){
        return (MultOpFactor) super.getNextNode();
    }
}
