package parser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Chris on 1/9/2016.
 */
public enum TokenType {
     NUM, ID, BLIT, ENDFILE, ERROR, AND , BOOL , BRANCH , CASE , CONTINUE , DEFAULT , ELSE , END , EXIT , IF , INT , LOOP ,
     MOD , NOT , OR , REF , RETURN , VOID, PLUS , MINUS , MULT , DIV , ANDTHEN ,ORELSE , LT , LTEQ , GT , GTEQ , EQ , NEQ ,
    ASSIGN, COLON, SEMI, COMMA, LPAREN, RPAREN, LSQR, RSQR, LCRLY, RCRLY, TokenType;

    private static final List<Type> intOperandTypes = Arrays.asList(Type.INT);
    private static final List<Type> intORBoolOperandTypes = Arrays.asList(Type.INT,Type.BOOL);
    private static final List<Type> boolOperandTypes = Arrays.asList(Type.BOOL);


    private static final List<TokenType> intResultTypes = Arrays.asList(PLUS,MINUS,MOD,DIV,MULT);

    private static final List<TokenType> boolResultTypes = Arrays.asList(GT,LT,LTEQ,GTEQ,NEQ,AND,OR,ANDTHEN,OR,ORELSE,NOT);



    public Type getResultType(){

        if( intResultTypes.contains(this) )
            return Type.INT;
        if( boolResultTypes.contains(this) )
            return Type.BOOL;
        return Type.VOID;
    }

    public List<Type> getOperandTypes(){

        switch(this){
            case MULT:case DIV:case PLUS:case MINUS:case GT:case LT:case GTEQ:case LTEQ:case MOD: return intOperandTypes;
            case EQ:case NEQ: return intORBoolOperandTypes;
            case OR:case ORELSE:case AND:case ANDTHEN: return boolOperandTypes;
        }

        return Arrays.asList(Type.VOID);
    }
}
