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
    protected int numberOfParameters = -1;
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
        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\ttype  : ").append(getType());
        sb.append(tabsStr).append("\tid    : ").append(getID().getName());

        sb.append(tabsStr).append("\tparams: ");
        params.appendContents(sb,tabs+2);

        sb.append(tabsStr).append("\tbody  : ");
        body.appendContents(sb,tabs+2);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }
}
