package parser.grammar.expressions;

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
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        String tabsStr = getTabs(tabs);
        sb.append(tabsStr);
        sb.append(getLine()).append(":").append(getClass().getSimpleName());

        if(uminus)
            sb.append(" - ");

        sb.append('\n').append(tabsStr).append("Type:").append(getType());
        term.appendContents(sb, tabs + 1);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }

    public AddOpTerm getNextNode(){
        return (AddOpTerm) super.getNextNode();
    }
}
