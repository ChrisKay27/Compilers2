package parser.grammar.statements;

import scanner.Token;

/**
 * Created by Chris on 1/30/2016.
 */
public class CaseStatement extends Statement {

    private final Statement statement;
    private final Token numberToken;

    public CaseStatement(int line, Token t, Statement statement) {
        super(line);
        this.numberToken = t;
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    public Token getNumberToken() {
        return numberToken;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\tvalue: ").append((numberToken == null) ? "default" : numberToken);

        statement.appendContents(sb, (tabs + 2));

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }
}
