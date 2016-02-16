package parser.grammar;

import parser.TokenType;

import java.util.HashSet;
import java.util.Set;

import static parser.TokenType.*;

/**
 * Created by Chris on 1/30/2016.
 */
public class FirstAndFollowSets {
    public static final Set<TokenType> FIRSTofStatement;

    static {
        Set<TokenType> FIRST = new HashSet<>();
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

    public static final Set<TokenType> FIRSTofLoop_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(LOOP);
        FIRSTofLoop_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofExit_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(EXIT);
        FIRSTofExit_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofContinue_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(CONTINUE);
        FIRSTofContinue_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofReturn_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(RETURN);
        FIRSTofReturn_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofNull_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(SEMI);
        FIRSTofNull_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofBranch_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(BRANCH);
        FIRSTofBranch_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofCase_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(CASE);
        FIRSTofCase_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofUMinus;
    static{
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(MINUS);
        FIRSTofUMinus = FIRST;
    }

    public static final Set<TokenType> FIRSTofMultop;
    static{
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(MULT);
        FIRST.add(DIV);
        FIRST.add(MOD);
        FIRST.add(AND);
        FIRST.add(ANDTHEN);
        FIRSTofMultop = FIRST;
    }

    public static final Set<TokenType> FIRSTofAddOp;
    static{
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(PLUS);
        FIRST.add(MINUS);
        FIRST.add(OR);
        FIRST.add(ORELSE);
        FIRSTofAddOp = FIRST;
    }

    public static final Set<TokenType> FIRSTofRelop;
    static{
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(LTEQ);
        FIRST.add(LT);
        FIRST.add(GT);
        FIRST.add(GTEQ);
        FIRST.add(EQ);
        FIRST.add(NEQ);
        FIRSTofRelop = FIRST;
    }

    public static final Set<TokenType> FIRSTofVar_tail;
    static{
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(LSQR);
        FIRSTofVar_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofId_tail;
    static{
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofVar_tail);
        //FIRST.addAll(FIRSTofCallTail);
        FIRSTofId_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofId_factor;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofId_factor = FIRST;
    }

    public static final Set<TokenType> FIRSTofNid_factor;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(NOT);
        FIRST.add(LPAREN);
        FIRST.add(NUM);
        FIRST.add(BLIT);
        FIRSTofNid_factor = FIRST;
    }


    public static final Set<TokenType> FIRSTofFactor;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofNid_factor);
        FIRST.addAll(FIRSTofId_factor);
        FIRSTofFactor = FIRST;
    }


    public static final Set<TokenType> FIRSTofTerm;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofFactor);
        FIRSTofTerm = FIRST;
    }



    public static final Set<TokenType> FIRSTofAdd_expr;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(MINUS);
        FIRST.addAll(FIRSTofTerm);
        FIRSTofAdd_expr = FIRST;
    }
    public static final Set<TokenType> FIRSTofExpression;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofAdd_expr);
        FIRSTofExpression = FIRST;
    }

    public static final Set<TokenType> FIRSTofIf_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(IF);
        FIRSTofIf_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofCompound_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(LCRLY);
        FIRSTofCompound_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofArguments;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofExpression);
        FIRSTofArguments = FIRST;
    }

    public static final Set<TokenType> FIRSTofCall_tail;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(LPAREN);
        FIRSTofCall_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofCall_stmt_tail;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofCall_tail);
        FIRSTofCall_stmt_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofAssign_stmt_tail;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(LSQR);
        FIRST.add(ASSIGN);
        FIRSTofAssign_stmt_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofId_stmt_tail;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofAssign_stmt_tail);
        FIRST.addAll(FIRSTofCall_stmt_tail);
        FIRSTofId_stmt_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofId_stmt;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofId_stmt = FIRST;
    }

    public static final Set<TokenType> FIRSTofNonvoid_specifier;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(INT);
        FIRST.add(BOOL);
        FIRSTofNonvoid_specifier = FIRST;
    }

    public static final Set<TokenType> FIRSTofParam;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofNonvoid_specifier);
        FIRST.add(REF);
        FIRSTofParam = FIRST;
    }

    public static final Set<TokenType> FIRSTofParams;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofParam);
        FIRSTofParams = FIRST;
    }

    public static final Set<TokenType> FIRSTofVar_name;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(ID);
        FIRSTofVar_name = FIRST;
    }

    public static final Set<TokenType> FIRSTofFun_dec_tail;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(LPAREN);
        FIRSTofFun_dec_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofVar_dec_tail;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(TokenType.LSQR);
        FIRST.add(TokenType.COMMA);
        FIRST.add(TokenType.SEMI);
        FIRSTofVar_dec_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofDec_tail;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofVar_dec_tail);
        FIRST.addAll(FIRSTofFun_dec_tail);
        FIRSTofDec_tail = FIRST;
    }

    public static final Set<TokenType> FIRSTofDeclaration;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.add(VOID);
        FIRST.addAll(FIRSTofNonvoid_specifier);
        FIRSTofDeclaration = FIRST;
    }

    public static final Set<TokenType> FIRSTofProgram;

    static {
        Set<TokenType> FIRST = new HashSet<>();
        FIRST.addAll(FIRSTofDeclaration);
        FIRSTofProgram = FIRST;
    }
}
