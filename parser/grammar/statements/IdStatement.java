package parser.grammar.statements;

import parser.grammar.ASTNode;
import parser.grammar.Statement;
import scanner.Token;

/**
 * Created by Chris on 2/12/2016.
 */
public class IdStatement extends Statement {
    private final Token idToken;
    private final StatementTail id_stmt_tail;

    public IdStatement(Token idToken, StatementTail id_stmt_tail) {

        this.idToken = idToken;
        this.id_stmt_tail = id_stmt_tail;
    }

    public Token getIdToken() {
        return idToken;
    }

    public StatementTail getId_stmt_tail() {
        return id_stmt_tail;
    }
}
