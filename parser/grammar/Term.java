package parser.grammar;

import parser.TokenType;

/**
 * Created by Chris on 2/12/2016.
 */
public class Term extends ASTNode {
    private final ASTNode factor;
    private final TokenType multop;
    private final ASTNode factor2;

    public Term(ASTNode factor, TokenType multop, ASTNode factor2) {
        this.factor = factor;
        this.multop = multop;
        this.factor2 = factor2;
    }
}
