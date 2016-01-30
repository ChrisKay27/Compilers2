package parser;

import scanner.Token;

/**
 * Created by Chris on 1/30/2016.
 */
public class Declaration extends AbstractSyntaxTreeNode {

    protected final Type type;
    protected final Token ID;

    public Declaration(Type type, Token ID) {
        this.type = type;
        this.ID = ID;
    }

    public Type getType() {
        return type;
    }

    public Token getID() {
        return ID;
    }
}
