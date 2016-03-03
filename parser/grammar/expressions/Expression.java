package parser.grammar.expressions;

import parser.TokenType;
import parser.Type;

/**
 * Created by Chris on 1/30/2016.
 */
public class Expression extends SubExpression {
    private final AddExpression addExp;
    private final TokenType relop;
    private final AddExpression addExp2;

    private Type type;


    public Expression(AddExpression addExpression, TokenType relop, AddExpression addExp2) {
        this.addExp = addExpression;
        this.relop = relop;
        this.addExp2 = addExp2;
    }

    public AddExpression getAddExp() {
        return addExp;
    }

    public TokenType getRelop() {
        return relop;
    }

    public AddExpression getAddExp2() {
        return addExp2;
    }

    @Override
    public void appendContents(StringBuilder sb , int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName());

        addExp.appendContents(sb,tabs+1);
        if( relop != null ) {
            sb.append('\n').append(getTabs(tabs)).append(relop);
            addExp2.appendContents(sb, tabs + 1);
        }
        if( nextNode != null )
            nextNode.appendContents(sb,tabs);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
