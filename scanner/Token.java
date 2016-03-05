package scanner;

import parser.TokenType;

/**
 *
 * Created by Chris on 1/9/2016.
 */
public class Token {

    /**
     * This token is here to represent a comment. It is returned from the state machine then thrown away by the Scanner.
     * It is never actually passed to the scanner.
     */
    public static final Token COMMENT_TOKEN = new Token(null, null);

    public TokenType token;
    public Object attrValue;
    public String name;

    public Token(TokenType token, String attrValue) {
        this.token = token;
        this.attrValue = attrValue;
        this.name = attrValue;
    }
    public Token(TokenType token, Object attrValue) {
        this.token = token;
        this.attrValue = attrValue;
    }
    @Override
    public String toString() {
        return token == null ? "Comment Token" : '(' + token.toString() + "," + attrValue + ")"
                + (name != null?" => " + name:"");
        //return "new Token(TokenType."+this.token.toString()+","+this.attrValue +")";
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
            return true;
        }


        return attrValue != null && attrValue.equals(t.attrValue);
    }

    public int getAttrValue() {
        return (int) attrValue;
    }
}
