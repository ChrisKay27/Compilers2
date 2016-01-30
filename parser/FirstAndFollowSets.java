package parser;

import java.util.HashSet;
import java.util.Set;

import static parser.Tokens.*;

/**
 * Created by Chris on 1/30/2016.
 */
public class FirstAndFollowSets {
    public static final Set<Tokens> FIRSTofStatement;

    static {
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

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(LOOP);
        FIRSTofLoop = FIRST;
    }

    public static final Set<Tokens> FIRSTofExit;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(EXIT);
        FIRSTofExit = FIRST;
    }

    public static final Set<Tokens> FIRSTofContinue;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(CONTINUE);
        FIRSTofContinue = FIRST;
    }

    public static final Set<Tokens> FIRSTofReturn;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(RETURN);
        FIRSTofReturn = FIRST;
    }

    public static final Set<Tokens> FIRSTofNull;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(SEMI);
        FIRSTofNull = FIRST;
    }

    public static final Set<Tokens> FIRSTofBranch;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(BRANCH);
        FIRSTofBranch = FIRST;
    }

    public static final Set<Tokens> FIRSTofCase;

    static {
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

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofIDFactor = FIRST;
    }

    public static final Set<Tokens> FIRSTofNidFactor;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(NOT);
        FIRST.add(LPAREN);
        FIRST.add(NUM);
        FIRST.add(BLIT);
        FIRSTofNidFactor = FIRST;
    }


    public static final Set<Tokens> FIRSTofFactor;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofNidFactor);
        FIRST.addAll(FIRSTofIDFactor);
        FIRSTofFactor = FIRST;
    }


    public static final Set<Tokens> FIRSTofTerm;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofFactor);
        FIRSTofTerm = FIRST;
    }



    public static final Set<Tokens> FIRSTofAddExpr;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(MINUS);
        FIRST.addAll(FIRSTofTerm);
        FIRSTofAddExpr = FIRST;
    }
    public static final Set<Tokens> FIRSTofExpression;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofAddExpr);
        FIRSTofExpression = FIRST;
    }

    public static final Set<Tokens> FIRSTofIfstmt;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(IF);
        FIRSTofIfstmt = FIRST;
    }

    public static final Set<Tokens> FIRSTofCompoundstmt;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(LCRLY);
        FIRSTofCompoundstmt = FIRST;
    }

    public static final Set<Tokens> FIRSTofArguments;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofExpression);
        FIRSTofArguments = FIRST;
    }

    public static final Set<Tokens> FIRSTofCalltail;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(LPAREN);
        FIRSTofCalltail = FIRST;
    }

    public static final Set<Tokens> FIRSTofCallstmttail;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofCalltail);
        FIRSTofCallstmttail = FIRST;
    }

    public static final Set<Tokens> FIRSTofAssignstmttail;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofAddExpr);
        FIRST.add(ASSIGN);
        FIRSTofAssignstmttail = FIRST;
    }

    public static final Set<Tokens> FIRSTofIdstmttail;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofAssignstmttail);
        FIRST.addAll(FIRSTofCallstmttail);
        FIRSTofIdstmttail = FIRST;
    }

    public static final Set<Tokens> FIRSTofIdstmt;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofIdstmt = FIRST;
    }

    public static final Set<Tokens> FIRSTofNonvoidspecifier;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(INT);
        FIRST.add(BOOL);
        FIRSTofNonvoidspecifier = FIRST;
    }

    public static final Set<Tokens> FIRSTofParam;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofNonvoidspecifier);
        FIRST.add(REF);
        FIRSTofParam = FIRST;
    }

    public static final Set<Tokens> FIRSTofParams;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofParam);
        FIRSTofParams = FIRST;
    }

    public static final Set<Tokens> FIRSTofVarname;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofVarname = FIRST;
    }

    public static final Set<Tokens> FIRSTofFundectail;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(LPAREN);
        FIRSTofFundectail = FIRST;
    }

    public static final Set<Tokens> FIRSTofVarDectail;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofAddExpr);
        FIRSTofVarDectail = FIRST;
    }

    public static final Set<Tokens> FIRSTofDectail;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofVarDectail);
        FIRST.addAll(FIRSTofFundectail);
        FIRSTofDectail = FIRST;
    }

    public static final Set<Tokens> FIRSTofDeclaration;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.add(VOID);
        FIRST.addAll(FIRSTofNonvoidspecifier);
        FIRSTofDeclaration = FIRST;
    }

    public static final Set<Tokens> FIRSTofProgram;

    static {
        Set<Tokens> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofDeclaration);
        FIRSTofProgram = FIRST;
    }
}
