package parser.grammar.expressions;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Chris on 1/30/2016.
 */
public class MinusExpression extends SubExpression {

    public MinusExpression(int line) {
        super(line);
    }

    @Override
    protected int evaluateStaticInt() {
        throw new NotImplementedException();
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(getLine()).append(": ").append(getClass().getSimpleName());

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
