package parser.grammar.statements;

import parser.Type;
import parser.grammar.ASTNode;
import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 2/12/2016.
 */
public class CallStatementTail extends StatementTail {
    private final Expression call_tail;

    public CallStatementTail(String line, Expression call_tail) {
        super(line);
        this.call_tail = call_tail;
    }

    public ASTNode getCall_tail() {
        return call_tail;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        if( call_tail == null )
            return;
        sb.append("Arguments: ");
        call_tail.appendContents(sb,tabs+1);
    }

    public Type getType(){
        return call_tail.getType();
    }
}
