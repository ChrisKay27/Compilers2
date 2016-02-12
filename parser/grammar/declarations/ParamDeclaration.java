package parser.grammar.declarations;

import parser.Type;
import scanner.Token;

/**
 * Created by Carston on 2/12/2016.
 */
public class ParamDeclaration extends VarDeclaration {
    //static instances
    public static ParamDeclaration voidParam = new ParamDeclaration();
    //memebers
    protected boolean isReference;
    protected boolean isArray;

    //constructors
    private ParamDeclaration() {
        super(Type.VOID, null);
        this.isReference = false;
    }

    public ParamDeclaration(Type type, Token ID, boolean isArray, boolean isReference) {
        super(type, ID);
        this.isReference = isReference;
        this.isArray = isArray;
    }
}
