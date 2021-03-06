package parser.grammar.expressions;

import parser.grammar.ASTNode;
import parser.grammar.declarations.Declaration;
import scanner.Token;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Chris on 2/12/2016.
 */
public class IdFactor extends Factor {

    private final Token idToken;
    private final ASTNode idTail;

    private Declaration decl;

    public IdFactor(int line, Token idToken, ASTNode idTail) {
        super(line);
        this.idToken = idToken;
        this.idTail = idTail;
    }

    public Token getIdToken() {
        return idToken;
    }

    public ASTNode getIdTail() {
        return idTail;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName()).append(" ");
        sb.append(tabsStr).append("\tid  : ").append(getIdToken().getName());
        sb.append(tabsStr).append("\ttype: ").append(getType());

        if( idTail != null )
            idTail.appendContents(sb,tabs+1);

        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }

    public Declaration getDecl() {
        return decl;
    }

    public void setDecl(Declaration decl) {
        this.decl = decl;
        setType(decl.getType());
    }


    @Override
    protected int evaluateStaticInt() {
        throw new NotImplementedException();
    }
}
