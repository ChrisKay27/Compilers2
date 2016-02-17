package parser.grammar.declarations;

import parser.Type;
import parser.grammar.expressions.AddExpression;
import scanner.Token;

/**
 * Created by Carston on 2/12/2016.
 */
public class VarDeclaration extends Declaration {

    protected AddExpression arrayIndex;

    public VarDeclaration(Type type, Token ID) {
        super(type, ID);
        this.arrayIndex = null;
    }

    public VarDeclaration(Type type, Token ID, AddExpression arrayIndex) {
        super(type, ID);
        this.arrayIndex = arrayIndex;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append('\t');
        sb.append(getClass().getSimpleName()).append(' ').append(getType()).append(' ').append(getID());

        if( arrayIndex != null )
            arrayIndex.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
