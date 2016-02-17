package parser.grammar.statements;


/**
 * Created by Chris on 1/30/2016.
 */
public class LoopStatement extends Statement {
    private final Statement statement;

    public LoopStatement(Statement statement) {

        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append('\t');
        sb.append(getClass().getSimpleName());

        statement.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
