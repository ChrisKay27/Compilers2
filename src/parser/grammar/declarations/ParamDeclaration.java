package parser.grammar.declarations;

import parser.Type;
import scanner.Token;

/**
 * Created by Carston on 2/12/2016.
 */
public class ParamDeclaration extends VarDeclaration {
    //static instances
    public static ParamDeclaration voidParam = new ParamDeclaration(-1);
    //members
    protected boolean isReference;
    protected boolean isArray;


    //constructors
    private ParamDeclaration(int line) {
        super(line, Type.VOID, null);
        this.isReference = false;
    }

    public ParamDeclaration(int line, Type type, Token ID, boolean isArray, boolean isReference) {
        super(line, type, ID);
        this.isReference = isReference;
        this.isArray = isArray;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getLine() + ":" + getClass().getSimpleName());
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