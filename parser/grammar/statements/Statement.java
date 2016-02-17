package parser.grammar.statements;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 1/30/2016.
 */
public class Statement extends ASTNode {

    public Statement(){}

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
