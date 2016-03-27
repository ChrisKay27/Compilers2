package parser.grammar.expressions;

import util.WTFException;

/**
 * Created by Chris on 2/12/2016.
 */
public class Term extends SubExpression {

    private final SubExpression factor;

    public Term(int line, SubExpression factor) {
        super(line);
        this.factor = factor;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

//        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
//        sb.append(tabsStr).append("\ttype: ").append(getType());
        factor.appendContents(sb, tabs);

        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }

    public SubExpression getFactor() {
        return factor;
    }

    public MultOpFactor getNextNode(){
        return (MultOpFactor) super.getNextNode();
    }

    @Override
    protected int evaluateStaticInt() {
        int result = this.factor.evaluateStaticInt();
        if (this.getNextNode() != null) {
            int termVal = this.getNextNode().getFactor().evaluateStaticInt();
            switch (this.getNextNode().getMultOp()) {
                case MULT:
                    result *= termVal;
                    break;
                case DIV:
                    result /= termVal;
                    break;
                case MOD:
                    result %= termVal;
                    break;
                default:
                    throw new WTFException("bad operator in term");
            }
        }
        return result;
    }
}
