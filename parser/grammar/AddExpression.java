package parser.grammar;

import parser.TokenType;

/**
 * Created by Chris on 1/30/2016.
 */
public class AddExpression extends ASTNode {
    private final boolean uminus;
    private final Term term;

    private final TokenType addop;
    private final Term term2;

    public AddExpression(boolean uminus, Term term, TokenType addop, Term term2) {
        this.uminus = uminus;
        this.term = term;
        this.addop = addop;
        this.term2 = term2;
    }

    public boolean isUminus() {
        return uminus;
    }

    public Term getTerm() {
        return term;
    }

    public TokenType getAddop() {
        return addop;
    }

    public Term getTerm2() {
        return term2;
    }
}
