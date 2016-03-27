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
        String tabsStr = '\n'+getTabs(tabs);

        if( getID() == null )
            sb.append(tabsStr).append("void");
        else {
            sb.append(tabsStr).append("id: ").append(getID() != null ? getID().getName() : "void");
            sb.append(tabsStr).append("isRef: ").append(isReference);
            sb.append(tabsStr).append("type: ").append(getType());
            sb.append(tabsStr).append("isArray: ").append(isArray);
        }
        if( nextNode != null ) {
            sb.append('\n');
            nextNode.appendContents(sb, tabs);
        }
    }


    public boolean isReference() {
        return isReference;
    }

    public void setReference(boolean isReference) {
        this.isReference = isReference;
    }
}
