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

}
