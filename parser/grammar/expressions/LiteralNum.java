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
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getLine()).append(":").append(getClass().getSimpleName()).append(" VALUE(").append(this.toString()).append(')');
        sb.append('\n').append(getTabs(tabs)).append("Type:").append(getType());
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
