package parser.grammar.statements;

import parser.grammar.expressions.AddExpression;


/**
 * Created by Chris on 1/30/2016.
 */
public class BranchStatement extends Statement {
    private final AddExpression addexp;
    private final CaseStatement caseStmt;

    public BranchStatement(AddExpression addexp, CaseStatement caseStmt) {
        super();
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
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");

        sb.append(getClass().getSimpleName());
        addexp.appendContents(sb, tabs+1);
        caseStmt.appendContents(sb, tabs+1);
        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }
}
