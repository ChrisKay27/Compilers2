package parser.grammar.declarations;

import parser.Type;
import parser.grammar.expressions.AddExpression;
import scanner.Token;

/**
 * Created by Carston on 2/12/2016.
 */
public class VarDeclaration extends Declaration {

    protected AddExpression arraySize;

    public VarDeclaration(int line, Type type, Token ID) {
        super(line, type, ID);
        this.arraySize = null;
    }

    public VarDeclaration(int line, Type type, Token ID, AddExpression arraySize) {
        super(line, type, ID);
        this.arraySize = arraySize;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getLine() + ":" + getClass().getSimpleName()).append(' ').append(getType()).append(' ').append(getID());

        if( arraySize != null )
            arraySize.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }


    public boolean isAnArray() {
        return arraySize != null;
    }
}
