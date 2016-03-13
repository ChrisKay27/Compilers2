package parser.grammar.expressions;

/**
 * Created by Chris on 2/12/2016.
 */
public abstract class Factor extends SubExpression {

protected Factor(int line) {
        super(line);
        }

public MultOpFactor getNextNode(){
        return (MultOpFactor) super.getNextNode();
        }
        }
