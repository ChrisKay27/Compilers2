package parser.grammar.declarations;

import parser.Type;
import scanner.Token;

/**
 * Created by Carston on 2/12/2016.
 */
public class ParamDeclaration extends VarDeclaration {
    //static instances
    public static ParamDeclaration voidParam = new ParamDeclaration();
    //members
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

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName() + " Line: " + getLine());
        sb.append(' ');
        if( isReference )
            sb.append(" ref ");

        sb.append(getType());

        if( isArray )
            sb.append(" array ");

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }


}
