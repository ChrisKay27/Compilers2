package scanner;

import parser.Tokens;

/**
 * Created by Chris on 1/9/2016.
 */
public class Token {

    public static final Token COMMENT_TOKEN = new Token(null, null);

    public Tokens token;
    public Object attrValue;

    public Token(Tokens token, Object attrValue) {
        this.token = token;
        this.attrValue = attrValue;
    }

    @Override
    public String toString() {
        return token == null ? "Comment Token" : '(' + token.toString() + "," + attrValue + ')';
    }
}
