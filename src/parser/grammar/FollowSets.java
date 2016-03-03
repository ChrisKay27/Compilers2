package parser.grammar;

import parser.TokenType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static parser.TokenType.*;
import static parser.grammar.FirstSets.*;

/**
 * Created by Chris on 1/30/2016.
 */
public class FollowSets {
    public static final Set<TokenType> FOLLOWofStatement;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(Arrays.asList(LCRLY,IF,LOOP,EXIT,CONTINUE,RETURN,SEMI,ID,RCRLY,ELSE,END,BRANCH,CASE,DEFAULT));
        FOLLOWofStatement = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofLoop_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(Arrays.asList(LCRLY,IF,LOOP,EXIT,CONTINUE,RETURN,SEMI,ID,RCRLY,ELSE,END,BRANCH,CASE,DEFAULT));
        FOLLOWofLoop_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofExit_stmt;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofExit_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofContinue_stmt;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofContinue_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofReturn_stmt;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofReturn_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofNull_stmt;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofNull_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofBranch_stmt;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofBranch_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofCase_stmt;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofCase_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofUMinus;
    static{
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(MINUS);
        FOLLOWofUMinus = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofMultop;
    static{
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(MULT);
        FOLLOW.add(DIV);
        FOLLOW.add(MOD);
        FOLLOW.add(AND);
        FOLLOW.add(ANDTHEN);
        FOLLOWofMultop = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofAddOp;
    static{
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(PLUS);
        FOLLOW.add(MINUS);
        FOLLOW.add(OR);
        FOLLOW.add(ORELSE);
        FOLLOWofAddOp = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofRelop;
    static{
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(LTEQ);
        FOLLOW.add(LT);
        FOLLOW.add(GT);
        FOLLOW.add(GTEQ);
        FOLLOW.add(EQ);
        FOLLOW.add(NEQ);
        FOLLOWofRelop = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofVar_tail;
    static{
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FIRSTofRelop);
        FOLLOW.addAll(FIRSTofAddOp);
        FOLLOW.addAll(FIRSTofMultop);
        FOLLOW.addAll(Arrays.asList(RPAREN,RSQR,SEMI,COMMA));
        FOLLOWofVar_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofId_tail;
    static{
        FOLLOWofId_tail = FOLLOWofVar_tail;
    }

    public static final Set<TokenType> FOLLOWofId_factor;

    static {
        FOLLOWofId_factor = FOLLOWofVar_tail;
    }

    public static final Set<TokenType> FOLLOWofNid_factor;

    static {
        FOLLOWofNid_factor = FOLLOWofVar_tail;
    }


    public static final Set<TokenType> FOLLOWofFactor;

    static {
        FOLLOWofFactor = FOLLOWofVar_tail;
    }


    public static final Set<TokenType> FOLLOWofTerm;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FIRSTofAddOp);
        FOLLOW.addAll(FIRSTofRelop);
        FOLLOW.addAll(Arrays.asList(RPAREN,RSQR,SEMI,COMMA));
        FOLLOWofTerm = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofAdd_expr;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FIRSTofRelop);
        FOLLOW.addAll(Arrays.asList(RPAREN,RSQR,SEMI,COMMA));
        FOLLOWofAdd_expr = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofExpression;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(RPAREN);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofExpression = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofIf_stmt;

    static {
        Set<TokenType> FOLLOW = FOLLOWofVar_tail;
        FOLLOWofIf_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofCompound_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofVar_tail);
        FOLLOW.addAll(Arrays.asList(INT,BOOL,VOID,ENDFILE));
        FOLLOWofCompound_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofArguments;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(RPAREN);
        FOLLOWofArguments = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofCall_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.addAll(FOLLOWofAddOp);
        FOLLOW.addAll(FOLLOWofMultop);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofCall_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofCall_stmt_tail;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofCall_stmt_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofAssign_stmt_tail;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofAssign_stmt_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofId_stmt_tail;

    static {
        Set<TokenType> FOLLOW = FOLLOWofLoop_stmt;
        FOLLOWofId_stmt_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofId_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(ID);
        FOLLOWofId_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofNonvoid_specifier;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(INT);
        FOLLOW.add(BOOL);
        FOLLOWofNonvoid_specifier = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofParam;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(COMMA);
        FOLLOW.add(RPAREN);
        FOLLOWofParam = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofParams;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(RPAREN);
        FOLLOWofParams = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofVar_name;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(COMMA);
        FOLLOW.add(SEMI);
        FOLLOWofVar_name = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofFun_dec_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(Arrays.asList(INT,BOOL,VOID,ENDFILE));
        FOLLOWofFun_dec_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofVar_dec_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(Arrays.asList(INT,BOOL,VOID,ENDFILE,LCRLY,IF,LOOP,EXIT,CONTINUE, RETURN,SEMI,ID,BRANCH));
        FOLLOWofVar_dec_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofDec_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(Arrays.asList(INT,BOOL,VOID,ENDFILE));
        FOLLOWofDec_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofDeclaration;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(Arrays.asList(INT,BOOL,VOID,ENDFILE));
        FOLLOWofDeclaration = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofProgram;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(ENDFILE);
        FOLLOWofProgram = FOLLOW;
    }
}
