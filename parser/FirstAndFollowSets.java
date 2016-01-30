package parser;

import com.sun.org.apache.xpath.internal.operations.Mod;

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

    public static final Set<Tokens> FIRSTofUMinus;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(MINUS);
        FIRSTofUMinus = FIRST;
    }

    public static final Set<Tokens> FIRSTofMultop;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(MULT);
        FIRST.add(DIV);
        FIRST.add(MOD);
        FIRST.add(AND);
        FIRST.add(ANDTHEN);
        FIRSTofMultop = FIRST;
    }

    public static final Set<Tokens> FIRSTofAddOp;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(PLUS);
        FIRST.add(MINUS);
        FIRST.add(OR);
        FIRST.add(ORELSE);
        FIRSTofAddOp = FIRST;
    }

    public static final Set<Tokens> FIRSTofRelop;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(LTEQ);
        FIRST.add(LT);
        FIRST.add(GT);
        FIRST.add(GTEQ);
        FIRST.add(EQ);
        FIRST.add(NEQ);
        FIRSTofRelop = FIRST;
    }

    public static final Set<Tokens> FIRSTofVarTail;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(LSQR);
        FIRSTofVarTail = FIRST;
    }

    public static final Set<Tokens> FIRSTofIDTail;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofVarTail);
        //FIRST.addAll(FIRSTofCallTail);
        FIRSTofIDTail = FIRST;
    }

    public static final Set<Tokens> FIRSTofIDFactor;
    static{
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofIDFactor = FIRST;
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





}
