package scanner;

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

    //Kept as an object with and added a getAttrValue() method which returns the attrValue casted to an int
    //This is an object in because our state machine
    public Object attrValue;
    public int id;
    public String name;

    public Token(TokenType token, String attrValue) {
        this.token = token;
        this.attrValue = attrValue;
        this.name = attrValue;
    }
    public Token(TokenType token, int id) {
        this.token = token;
        this.attrValue = id;
        this.id = id;
    }
//    public Token(TokenType token, Object attrValue) {
//        this.token = token;
//        this.attrValue = attrValue;
//    }
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
        return id;
    }

    public String getName() {
        return name;
    }
}
