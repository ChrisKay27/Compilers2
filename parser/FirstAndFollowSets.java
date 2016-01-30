package parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chris on 1/30/2016.
 */
public class FirstAndFollowSets {

    public static final Set<Tokens> FIRSTofStatement = new HashSet<>();
    static{
        FIRSTofStatement.add(Tokens.ID);
        FIRSTofStatement.add(Tokens.LPAREN);
        FIRSTofStatement.add(Tokens.IF);
        FIRSTofStatement.add(Tokens.LOOP);
        FIRSTofStatement.add(Tokens.EXIT);
        FIRSTofStatement.add(Tokens.CONTINUE);
        FIRSTofStatement.add(Tokens.RETURN);
        FIRSTofStatement.add(Tokens.SEMI);
        FIRSTofStatement.add(Tokens.BRANCH);
    }



}
