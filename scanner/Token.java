package scanner;

/**
 *
 * Created by Chris on 1/9/2016.
 */
public class Token {

    /**
     * This token is here to represent a comment. It is returned from the state machine then thrown away by the Scanner.
     * It is never actually passed to the parser.
     */
    public static final Token COMMENT_TOKEN = new Token(null, null);

    public TokenType token;

    public int id;
    public String name;

    public Token(TokenType token, String attrValue) {
        this.token = token;
        this.name = attrValue;
    }
    public Token(TokenType token, int id) {
        this.token = token;
        this.id = id;
    }

    @Override
    public String toString() {
        return token == null ? "Comment Token" : '(' + token.toString() + "," + id + ")"
                + (name != null?" => " + name:"");
        //return "new Token(TokenType."+this.token.toString()+","+this.attrValue +")";
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) return true;
        if (!(obj instanceof Token))
            return false;

        Token t = (Token) obj;
        if (t.token != token)
            return false;

        if( t.id == id )
            return true;

        return name != null && name.equals(t.name);
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
