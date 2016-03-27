package parser.grammar.expressions;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LiteralBool extends NidFactor {
    private final int bool;

    public LiteralBool(int line, int bool) {
        super(line);
        if(!(bool == 1 || bool == 0)) throw new RuntimeException("Bad Boolean Value :" + bool);
        this.bool = bool;
    }

    public int isBool() {
        return bool;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName()).append(" VALUE(").append(this.toString()).append(')');
        sb.append(tabsStr).append("\ttype: ").append(getType());
        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }

    public int getValue(){
        return bool;
    }

    public String toString(){
        if(bool == 1) return ""+true;
        if(bool == 0) return ""+false;
        return "BROKEN_BOOL";
    }

    @Override
    protected int evaluateStaticInt() {
        throw new NotImplementedException();
    }
}
