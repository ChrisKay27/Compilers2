package parser.grammar.expressions;

import scanner.TokenType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Carston on 2/21/2016.
 */
public class AddOpTerm extends SubExpression {

    private final TokenType addOp;

    private final Term term;

    public AddOpTerm(int line, TokenType addOp, Term term) {
        super(line);
        this.addOp = addOp;
        this.term = term;
    }

    public TokenType getAddOp() { return addOp; }

    public Term getTerm() { return term; }


    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n' + getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\ttype: ").append(getType());
        sb.append(tabsStr).append("\taddop: ").append(addOp);
        term.appendContents(sb, tabs);
        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }

    @Override
    protected int evaluateStaticInt() {
        throw new NotImplementedException();
    }
}
