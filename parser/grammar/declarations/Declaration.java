package parser.grammar.declarations;

import parser.Type;
import parser.grammar.ASTNode;
import scanner.Token;

/**
 * Created by Chris on 1/30/2016.
 */
public abstract class Declaration extends ASTNode {

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

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");

        sb.append(getClass().getSimpleName()).append(' ').append(type).append(' ').append(ID);
        if( nextNode != null )
            nextNode.appendContents(sb, tabs);
    }
}
