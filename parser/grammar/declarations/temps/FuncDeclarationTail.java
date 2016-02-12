package parser.grammar.declarations.temps;

import parser.grammar.CompoundStatement;
import parser.grammar.declarations.Declaration;
import parser.grammar.declarations.ParamDeclaration;

/**
 * Created by Chris on 1/30/2016.
 */
public class FuncDeclarationTail extends DecTail {

    protected final ParamDeclaration params;
    protected final CompoundStatement funcBody;

    public FuncDeclarationTail(ParamDeclaration params, CompoundStatement body) {
        this.params = params;
        this.funcBody = body;
    }

    public ParamDeclaration getParams() {
        return params;
    }

    public CompoundStatement getFuncBody() {
        return funcBody;
    }
}
