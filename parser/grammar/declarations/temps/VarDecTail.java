package parser.grammar.declarations.temps;

import parser.Type;
import parser.grammar.declarations.VarDeclaration;
import parser.grammar.expressions.AddExpression;
import scanner.Token;

import java.util.List;

/**
 * Created by Chris on 2/12/2016.
 */
public class VarDecTail implements DecTail {
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

    public VarDeclaration toVarDeclarations(Type type, Token id){
        VarDeclaration head = new VarDeclaration(type, id, arrayLenthExpr);
        VarDeclaration current = head;
        VarDeclaration temp = null;
        for (VarName var : varNames) {
            temp = new VarDeclaration(type, var.getId(), var.getAddExpression());
            current.setNextNode(temp);
            current = temp;
        }
        return head;
    }
}
