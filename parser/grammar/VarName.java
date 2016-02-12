package parser.grammar;

import scanner.Token;

/**
 * Created by Chris on 2/12/2016.
 */
public class VarName {
    private final Token ID;
    private final AddExpression add_exp;

    public VarName(Token id, AddExpression add_exp) {
        ID = id;
        this.add_exp = add_exp;
    }
}
