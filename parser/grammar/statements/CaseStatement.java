package parser.grammar.statements;

import scanner.Token;

/**
 * Created by Chris on 1/30/2016.
 */
public class CaseStatement extends Statement {

    private final Statement statement;
    private final Token numberToken;

    public CaseStatement(String line, Token t, Statement statement) {
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
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName()).append(" : ").append((numberToken == null) ? "Default" : numberToken).append(" ");
        statement.appendContents(sb, (tabs + 1));
        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }
}
