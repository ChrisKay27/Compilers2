package parser.grammar.expressions;

import parser.Type;

public class LiteralNum extends NidFactor {
    private final int num;

    public LiteralNum(int line, int num) {
        super(line);
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\tvalue:").append(this.toString());
        sb.append(tabsStr).append("\ttype : ").append(getType());

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }

    public String toString(){
        return "" +this.num ;
    }

    @Override
    public Type getType() {
        return Type.INT;
    }

    @Override
    protected int evaluateStaticInt() {
        return num;
    }
}
