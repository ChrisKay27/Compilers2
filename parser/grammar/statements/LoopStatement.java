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
}
