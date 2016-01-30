package parser;

/**
 * Created by Carston on 1/30/2016.
 */

import scanner.Token;

public class Grammar {

    public boolean matchToken(Token t){

        return (t.equals(parser.nextToken()));
    }

    public boolean program(){
        boolean found = true;
        do {
            found &= declaration();
        } while(found);
        return true;
    }


}
