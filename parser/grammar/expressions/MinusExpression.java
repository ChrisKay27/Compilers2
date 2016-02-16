package parser.grammar.expressions;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 1/30/2016.
 */
public class MinusExpression extends ASTNode {

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append('\t');
        sb.append(getClass().getSimpleName());

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
