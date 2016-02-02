package parser.grammar;

/**
 * Created by Chris on 1/30/2016.
 */
public class BranchStatement extends AST {
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
}
