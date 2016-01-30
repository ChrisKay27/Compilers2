package parser;

import scanner.Token;

/**
 * Created by Chris on 1/30/2016.
 */
public class FuncDeclaration extends Declaration {

    private final Declaration params;

    private final Statement body;

    public FuncDeclaration(Type type, Token ID, Declaration params, Statement body) {
        super(type,ID);
        this.ID = ID;
        this.params = params;
        this.body = body;
    }


    public Type getType() {
        return type;
    }

    public Token getID() {
        return ID;
    }

    public Declaration getParams() {
        return params;
    }
}
