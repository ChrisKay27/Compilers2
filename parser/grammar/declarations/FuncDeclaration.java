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

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append('\t');
        sb.append(getClass().getSimpleName()).append(' ').append(getType()).append(' ').append(getID());

        if( params != null )
            params.appendContents(sb,tabs+1);

        if( body != null )
            body.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
