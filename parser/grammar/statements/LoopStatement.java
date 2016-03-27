package parser.grammar.statements;


/**
 * Created by Chris on 1/30/2016.
 */
public class LoopStatement extends Statement {
    private final Statement statement;

    public LoopStatement(int line, Statement statement) {
        super(line);
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());

        statement.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
