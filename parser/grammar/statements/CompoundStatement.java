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
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getLine() + ":" + getClass().getSimpleName());

        if( declarations != null )
            declarations.appendContents(sb, tabs + 1);
        if( statements != null )
            statements.appendContents(sb, tabs + 1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
