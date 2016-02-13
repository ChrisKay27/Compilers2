package parser.grammar.declarations;

import parser.Type;
import parser.grammar.statements.CompoundStatement;
import scanner.Token;

/**
 *
 * Created by Chris on 1/30/2016.
 */
public class FuncDeclaration extends Declaration {

    private final ParamDeclaration params;
    private final CompoundStatement body;

    public FuncDeclaration(Type type, Token ID, ParamDeclaration params, CompoundStatement body) {
        super(type,ID);
        this.params = params;
        this.body = body;
    }

    public ParamDeclaration getParams() {
        return params;
    }

    public CompoundStatement getBody() {
        return body;
    }
}
