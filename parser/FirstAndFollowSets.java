package parser;

import java.util.HashSet;
import java.util.Set;

import static parser.Tokens.MINUS;
import static parser.Tokens.*;

/**
 * Created by Chris on 1/30/2016.
 */
public class FirstAndFollowSets {

    public static final Set<Tokens> FIRSTofStatement;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRST.add(LPAREN);
        FIRST.add(IF);
        FIRST.add(LOOP);
        FIRST.add(EXIT);
        FIRST.add(CONTINUE);
        FIRST.add(RETURN);
        FIRST.add(SEMI);
        FIRST.add(BRANCH);
        FIRSTofStatement = FIRST;
    }


    public static final Set<Tokens> FIRSTofLoop;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(LOOP);
        FIRSTofLoop = FIRST;
    }

    public static final Set<Tokens> FIRSTofExit;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(EXIT);
        FIRSTofExit = FIRST;
    }

    public static final Set<Tokens> FIRSTofContinue;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(CONTINUE);
        FIRSTofContinue = FIRST;
    }

    public static final Set<Tokens> FIRSTofReturn;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(RETURN);
        FIRSTofReturn = FIRST;
    }

    public static final Set<Tokens> FIRSTofNull;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(SEMI);
        FIRSTofNull = FIRST;
    }

    public static final Set<Tokens> FIRSTofBranch;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(BRANCH);
        FIRSTofBranch = FIRST;
    }

    public static final Set<Tokens> FIRSTofCase;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(CASE);
        FIRSTofCase = FIRST;
    }

    public static final Set<Tokens> FIRSTofAddExpr;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(MINUS);
        FIRST.addAll(FIRSTofTerm);
        FIRSTofAddExpr = FIRST;
    }

    public static final Set<Tokens> FIRSTofExpression;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofAddExpr);
        FIRSTofExpression = FIRST;
    }

    public static final Set<Tokens> FIRSTofNidFactor;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(NOT);
        FIRST.add(LPAREN);
        FIRST.add(NUM);
        FIRST.add(BLIT);
        FIRSTofNidFactor = FIRST;
    }


    public static final Set<Tokens> FIRSTofIDFactor;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofIDFactor = FIRST;
    }

    public static final Set<Tokens> FIRSTofFactor;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofNidFactor);
        FIRST.addAll(FIRSTofIDFactor);
        FIRSTofFactor = FIRST;
    }

    public static final Set<Tokens> FIRSTofTerm;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofFactor);
        FIRSTofTerm = FIRST;
    }



}
