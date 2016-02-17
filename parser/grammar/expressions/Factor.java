package parser.grammar.expressions;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 2/12/2016.
 */
public class Factor extends ASTNode {

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName());

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
