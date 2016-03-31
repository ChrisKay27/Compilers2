package parser.grammar.expressions;

import parser.Type;
import parser.grammar.ASTNode;

/**
 * Created by Carston on 2/22/2016.
 */
public abstract class SubExpression extends ASTNode {
    private Type type;
    private boolean isStatic = true;

    protected SubExpression(int line) {
        super(line);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isStatic() {
        return isStatic;
    }

    protected abstract int evaluateStaticInt();

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

}
