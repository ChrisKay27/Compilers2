package parser.grammar;

import parser.TokenType;

import java.util.HashSet;
import java.util.Set;

import static parser.TokenType.*;

/**
 * Created by Chris on 1/30/2016.
 */
public class FollowSets {
    public static final Set<TokenType> FOLLOWofStatement;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(ID);
        FOLLOW.add(LPAREN);
        FOLLOW.add(IF);
        FOLLOW.add(LOOP);
        FOLLOW.add(EXIT);
        FOLLOW.add(CONTINUE);
        FOLLOW.add(RETURN);
        FOLLOW.add(SEMI);
        FOLLOW.add(BRANCH);
        FOLLOWofStatement = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofLoop_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(LOOP);
        FOLLOWofLoop_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofExit_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(EXIT);
        FOLLOWofExit_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofContinue_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(CONTINUE);
        FOLLOWofContinue_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofReturn_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(RETURN);
        FOLLOWofReturn_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofNull_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(SEMI);
        FOLLOWofNull_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofBranch_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(BRANCH);
        FOLLOWofBranch_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofCase_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();

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
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.addAll(FOLLOWofAddOp);
        FOLLOW.addAll(FOLLOWofMultop);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofVar_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofId_tail;
    static{
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.addAll(FOLLOWofAddOp);
        FOLLOW.addAll(FOLLOWofMultop);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofId_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofId_factor;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.addAll(FOLLOWofAddOp);
        FOLLOW.addAll(FOLLOWofMultop);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofId_factor = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofNid_factor;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.addAll(FOLLOWofAddOp);
        FOLLOW.addAll(FOLLOWofMultop);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofNid_factor = FOLLOW;
    }


    public static final Set<TokenType> FOLLOWofFactor;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.addAll(FOLLOWofAddOp);
        FOLLOW.addAll(FOLLOWofMultop);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofFactor = FOLLOW;
    }


    public static final Set<TokenType> FOLLOWofTerm;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.addAll(FOLLOWofAddOp);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
        FOLLOWofTerm = FOLLOW;
    }



    public static final Set<TokenType> FOLLOWofAdd_expr;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofRelop);
        FOLLOW.add(RPAREN);
        FOLLOW.add(RSQR);
        FOLLOW.add(SEMI);
        FOLLOW.add(COMMA);
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
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(IF);
        FOLLOWofIf_stmt = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofCompound_stmt;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(LCRLY);
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
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofCall_tail);
        FOLLOWofCall_stmt_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofAssign_stmt_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(LSQR);
        FOLLOW.add(ASSIGN);
        FOLLOWofAssign_stmt_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofId_stmt_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofAssign_stmt_tail);
        FOLLOW.addAll(FOLLOWofCall_stmt_tail);
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
        FOLLOW.add(ID);
        FOLLOWofVar_name = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofFun_dec_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(LPAREN);
        FOLLOWofFun_dec_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofVar_dec_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(TokenType.LSQR);
        FOLLOW.add(TokenType.COMMA);
        FOLLOW.add(TokenType.SEMI);
        FOLLOWofVar_dec_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofDec_tail;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofVar_dec_tail);
        FOLLOW.addAll(FOLLOWofFun_dec_tail);
        FOLLOWofDec_tail = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofDeclaration;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.add(VOID);
        FOLLOW.addAll(FOLLOWofNonvoid_specifier);
        FOLLOWofDeclaration = FOLLOW;
    }

    public static final Set<TokenType> FOLLOWofProgram;

    static {
        Set<TokenType> FOLLOW = new HashSet<>();
        FOLLOW.addAll(FOLLOWofDeclaration);
        FOLLOWofProgram = FOLLOW;
    }
}
