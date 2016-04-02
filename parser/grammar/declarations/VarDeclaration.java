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
    public void appendContents(StringBuilder sb, int tabs) {

        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\ttype: ").append(getType());
        sb.append(tabsStr).append("\tid  : ").append(getID().getName());

        if (arraySize != null) {
            sb.append(tabsStr).append("\tarray size: ");
            arraySize.appendContents(sb, tabs + 2);
        }

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }


    public boolean isAnArray() {
        return arraySize != null;
    }

    public AddExpression getArraySizeExpression() {
        return this.arraySize;
    }

    public int getArraySize() {
        return this.arraySize.evaluateStaticInt();
    }
}
