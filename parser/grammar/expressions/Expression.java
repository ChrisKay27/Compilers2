package parser.grammar.expressions;

import scanner.TokenType;
import parser.Type;

/**
 * Created by Chris on 1/30/2016.
 */
public class Expression extends SubExpression {
    private final AddExpression addExp;
    private final TokenType relop;
    private final AddExpression addExp2;

    private Type type;

    public Expression(int line, AddExpression addExpression, TokenType relop, AddExpression addExp2) {
        super(line);
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
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

        int nTabs = tabs;
        if( relop != null ) {
            sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
            sb.append(tabsStr).append("\ttype: ").append(getType());
            nTabs += 2 ;
        }

        addExp.appendContents(sb, nTabs);

        if (relop != null) {
            sb.append(tabsStr).append(getLine()).append(": ").append("Relop => ").append(relop);
            addExp2.appendContents(sb, nTabs);
        }

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    protected int evaluateStaticInt() {
        return this.addExp.evaluateStaticInt();
    }

    public Type getType() {
        return type;
    }
}
