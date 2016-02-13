package parser.grammar.expressions;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 2/12/2016.
 */
public class NotNidFactor extends NidFactor {

    private final ASTNode factor;

    public NotNidFactor(ASTNode factor) {
        this.factor = factor;
    }

    @Override
    public String toString() {
        return " Not " + factor;
    }
}
