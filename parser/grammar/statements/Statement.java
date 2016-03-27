package parser.grammar.statements;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 1/30/2016.
 */
public class Statement extends ASTNode {

    public Statement(int line) {super(line);}

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }
}
