package scanner;

import parser.Tokens;

/**
 * Created by Chris on 1/9/2016.
 */
public class Token {
    public Tokens token;
    public Object attrValue;

    public Token(Tokens token, Object attrValue) {
        this.token = token;
        this.attrValue = attrValue;
    }
}
