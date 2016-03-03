package parser.grammar.statements;

import parser.grammar.declarations.Declaration;
import scanner.Token;

/**
 * Created by Chris on 2/12/2016.
 */
public class IdStatement extends Statement {
    private final Token idToken;
    private final StatementTail id_stmt_tail;

    private Declaration decl;

    public IdStatement(String line, Token idToken, StatementTail id_stmt_tail) {
        super(line);
        this.idToken = idToken;
        this.id_stmt_tail = id_stmt_tail;
    }

    public Token getIdToken() {
        return idToken;
    }

    public StatementTail getId_stmt_tail() {
        return id_stmt_tail;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName() + " Line: " + getLine());
        sb.append(' ');

        sb.append(idToken);
        sb.append(' ');
        id_stmt_tail.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }

    public Declaration getDecl() {
        return decl;
    }

    public void setDecl(Declaration decl) {
        this.decl = decl;
    }
}
