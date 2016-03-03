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
    
    private final AddExpression arrayLengthExpr;
    private final List<VarName> varNames;

    public VarDecTail(AddExpression arrayLengthExpr, List<VarName> varNames) {
        this.arrayLengthExpr = arrayLengthExpr;
        this.varNames = varNames;
    }

    public AddExpression getArrayLenthExpr() {
        return arrayLengthExpr;
    }

    public List<VarName> getVarNames() {
        return varNames;
    }

    public VarDeclaration toVarDeclarations(String line, Type type, Token id) {
        VarDeclaration head = new VarDeclaration(line, type, id, arrayLengthExpr);
        VarDeclaration current = head;
        VarDeclaration temp;
        for (VarName var : varNames) {
            temp = new VarDeclaration(line, type, var.getId(), var.getAddExpression());
            current.setNextNode(temp);
            current = temp;
        }
        return head;
    }
}
