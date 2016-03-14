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
    protected int numberOfParameters;
    protected int numberOfLocals;
    private boolean hasReturnValue;

    public FuncDeclaration(int line, Type type, Token ID, ParamDeclaration params, CompoundStatement body) {
        super(line, type, ID);
        this.params = params;
        this.body = body;
    }

    public ParamDeclaration getParams() {
        return params;
    }

    public CompoundStatement getBody() {
        return body;
    }

    public void setNumberOfParameters(int numberOfParameters) {this.numberOfParameters = numberOfParameters;}
    public void setNumberOfLocals(int numberOfLocals) {this.numberOfLocals = numberOfLocals;}
    public int getNumberOfParameters(){return this.numberOfParameters;}
    public int getNumberOfLocals(){return this.numberOfLocals;}

    public boolean hasReturnValue(){
        return hasReturnValue;
    }

    public void setHasReturnValue(boolean hasReturnValue) {
        this.hasReturnValue = hasReturnValue;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName()).append(' ').append(getType()).append(' ').append(getID());
        sb.append('\n').append(getTabs(tabs)).append("Type:").append(getType());
        if( params != null )
            params.appendContents(sb,tabs+1);

        if( body != null )
            body.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
