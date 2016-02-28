package parser.grammar.expressions;

import parser.Type;
import parser.grammar.ASTNode;

/**
 * Created by Carston on 2/22/2016.
 */
public abstract class Subexpression extends ASTNode {
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
