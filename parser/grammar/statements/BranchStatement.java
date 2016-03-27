package parser.grammar.statements;

import parser.grammar.expressions.AddExpression;


/**
 * Created by Chris on 1/30/2016.
 */
public class BranchStatement extends Statement {
    private final AddExpression addexp;
    private final CaseStatement caseStmt;

    public BranchStatement(int line, AddExpression addexp, CaseStatement caseStmt) {
        super(line);
        this.addexp = addexp;
        this.caseStmt = caseStmt;
    }

    public AddExpression getAddexp() {
        return addexp;
    }

    public CaseStatement getCaseStmt() {
        return caseStmt;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\texpression: ");
        addexp.appendContents(sb, tabs+1);
        sb.append(tabsStr).append("\tcase statements: ");
        caseStmt.appendContents(sb, tabs+1);

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }
}
