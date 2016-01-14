package scanner;

import org.jetbrains.annotations.Nullable;
import parser.Tokens;

/**
 * Created by Chris on 1/9/2016.
 */
public class Token {

    public static final Token COMMENT_TOKEN = new Token(null, null);

    public Tokens token;
    @Nullable
    public Object attrValue;

    public Token(Tokens token, @Nullable Object attrValue) {
        this.token = token;
        this.attrValue = attrValue;
    }

    @Override
    public String toString() {
        return token == null ? "Comment Token" : '(' + token.toString() + "," + attrValue + ')';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Token)) {
            return false;
        }

        Token t = (Token) obj;
        if (t.token != token) {
            return false;
        }
        if( t.attrValue == attrValue ) {
            return true;}


        return attrValue != null && attrValue.equals(t.attrValue);
    }
}
