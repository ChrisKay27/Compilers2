package parser.grammar.expressions;

import scanner.TokenType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Carston on 2/21/2016.
 */
public class MultOpFactor extends SubExpression {

    private final TokenType addOp;
    private final SubExpression factor;

    public MultOpFactor(int line, TokenType addOp, SubExpression term) {
        super(line);
        this.addOp = addOp;
        this.factor = term;
    }

    public TokenType getMultOp() { return addOp; }

    public SubExpression getFactor() { return factor; }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {

        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr);
        sb.append(getLine()).append(": ").append(getClass().getSimpleName());

        sb.append(" ").append(addOp).append(" ");
        factor.appendContents(sb, tabs + 1);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }

    @Override
    protected int evaluateStaticInt() {
        throw new NotImplementedException();
    }
}
