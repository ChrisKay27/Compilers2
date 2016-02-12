package parser.grammar;

/**
 * Created by Chris on 1/30/2016.
 */
public class FuncDeclarationTail extends DecTail {

    protected final Declaration params;
    protected final CompoundStatement funcBody;

    public FuncDeclarationTail(Declaration params, CompoundStatement body) {
        this.params = params;
        this.funcBody = body;
    }

    public Declaration getParams() {
        return params;
    }

    public CompoundStatement getFuncBody() {
        return funcBody;
    }
}
