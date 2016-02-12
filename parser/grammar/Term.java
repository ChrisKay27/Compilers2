package parser.grammar;

import parser.TokenType;

/**
 * Created by Chris on 2/12/2016.
 */
public class Term extends ASTNode {
    private final Factor factor;
    private final TokenType multop;
    private final Factor factor2;

    public Term(Factor factor, TokenType multop, Factor factor2) {
        this.factor = factor;
        this.multop = multop;
        this.factor2 = factor2;
    }
}
