package parser.grammar.expressions;

import util.WTFException;

/**
 * Created by Chris on 1/30/2016.
 */
public class AddExpression extends SubExpression {
    private final boolean uminus;
    private final Term term;

    public AddExpression(int line, boolean uminus, Term term) {
        super(line);
        this.uminus = uminus;
        this.term = term;
        this.line = line;
    }

    public boolean isUminus() {
        return uminus;
    }

    public Term getTerm() {
        return term;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {

        String tabsStr = '\n' + getTabs(tabs);

        int nTabs = tabs;
        if (uminus) {
            sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());

            sb.append(tabsStr).append("\tuminus: true");

            sb.append(tabsStr).append("\ttype  : ").append(getType());

            nTabs += 2;
        }

        term.appendContents(sb, nTabs);

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }

    public AddOpTerm getNextNode() {
        return (AddOpTerm) super.getNextNode();
    }

    @Override
    public int evaluateStaticInt() {
        int result = this.term.evaluateStaticInt();
        if (isUminus()) {
            return result * -1;
        }
        if (this.getNextNode() != null) {
            int termVal = this.getNextNode().getTerm().evaluateStaticInt();
            switch (this.getNextNode().getAddOp()) {
                case PLUS:
                    result += termVal;
                    break;
                case MINUS:
                    result -= termVal;
                    break;
                default:
                    throw new WTFException("operator error?");
            }
        }
        return result;
    }
}
