package parser.grammar.statements;

import parser.grammar.declarations.Declaration;

/**
 *
 * Created by Chris on 2/2/2016.
 */
public class CompoundStatement extends Statement {

    private final Declaration declarations;
    private final Statement statements;

    public CompoundStatement(int line, Declaration declarations, Statement statements) {
        super(line);
        this.declarations = declarations;
        this.statements = statements;
    }

    public Declaration getDeclarations() {
        return declarations;
    }

    public Statement getStatements() {
        return statements;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());

        if( declarations != null ) {
            sb.append(tabsStr).append("\tdeclarations: ");
            declarations.appendContents(sb, tabs + 2);
        }

        sb.append(tabsStr).append("\tstatements: ");
        statements.appendContents(sb, tabs + 2);


        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
