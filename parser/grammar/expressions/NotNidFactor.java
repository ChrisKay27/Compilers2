package parser.grammar.expressions;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Chris on 2/12/2016.
 */
public class NotNidFactor extends NidFactor {

    private final SubExpression factor;

    public NotNidFactor(int line, SubExpression factor) {
        super(line);
        this.factor = factor;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {

        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\ttype: ").append(getType());
        factor.appendContents(sb, tabs + 2);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs + 2);
    }

    public SubExpression getFactor() {
        return factor;
    }

    @Override
    protected int evaluateStaticInt() {
        throw new NotImplementedException();
    }
}
