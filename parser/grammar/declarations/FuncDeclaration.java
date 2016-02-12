package parser.grammar.declarations;

import parser.Type;
import parser.grammar.CompoundStatement;
import parser.grammar.declarations.Declaration;
import scanner.Token;

/**
 *
 * Created by Chris on 1/30/2016.
 */
public class FuncDeclaration extends Declaration {

    private final Declaration params;
    private final CompoundStatement body;

    public FuncDeclaration(Type type, Token ID, ParamDeclaration params, CompoundStatement body) {
        super(type,ID);
        this.params = params;
        this.body = body;
    }

    public Declaration getParams() {
        return params;
    }

    public CompoundStatement getBody() {
        return body;
    }
}
