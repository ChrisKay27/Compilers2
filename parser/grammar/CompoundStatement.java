package parser.grammar;

import parser.grammar.declarations.Declaration;

/**
 *
 * Created by Chris on 2/2/2016.
 */
public class CompoundStatement extends Statement{
    private final Declaration declarations;
    private final Statement statements;

    public CompoundStatement(Declaration declarations, Statement statements) {
        this.declarations = declarations;
        this.statements = statements;
    }

    public Declaration getDeclarations() {
        return declarations;
    }

    public Statement getStatements() {
        return statements;
    }
}
