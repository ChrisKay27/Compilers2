package parser.grammar.statements;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 2/12/2016.
 */
public class CallStatementTail extends StatementTail {
    private final ASTNode call_tail;

    public CallStatementTail(ASTNode call_tail) {
        this.call_tail = call_tail;
    }

    public ASTNode getCall_tail() {
        return call_tail;
    }
}
