package parser.grammar.expressions;

import parser.grammar.ASTNode;
import scanner.Token;

/**
 * Created by Chris on 2/12/2016.
 */
public class IdFactor extends Factor {


    private final Token idToken;
    private final ASTNode idTail;

    public IdFactor(Token idToken, ASTNode idTail) {
        super();
        this.idToken = idToken;
        this.idTail = idTail;
    }

    public Token getIdToken() {
        return idToken;
    }

    public ASTNode getIdTail() {
        return idTail;
    }
}
