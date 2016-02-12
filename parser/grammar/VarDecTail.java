package parser.grammar;

import java.util.List;

/**
 * Created by Chris on 2/12/2016.
 */
public class VarDecTail extends DecTail {
    private final AddExpression arrayLenthExpr;
    private final List<VarName> varNames;

    public VarDecTail(AddExpression arrayLenthExpr, List<VarName> varNames) {
        this.arrayLenthExpr = arrayLenthExpr;
        this.varNames = varNames;
    }

    public AddExpression getArrayLenthExpr() {
        return arrayLenthExpr;
    }

    public List<VarName> getVarNames() {
        return varNames;
    }
}
