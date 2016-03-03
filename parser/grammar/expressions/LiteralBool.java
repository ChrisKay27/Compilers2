package parser.grammar.expressions;

public class LiteralBool extends NidFactor {
    private final int bool;

    public LiteralBool(int bool) {
        if(!(bool == 1 || bool == 0)) throw new RuntimeException("Bad Boolean Value :" + bool);
        this.bool = bool;
    }

    public int isBool() {
        return bool;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        sb.append('\n');
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        sb.append(getClass().getSimpleName()).append(" VALUE(").append(this.toString()).append(')');

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }

    public String toString(){
        if(bool == 1) return ""+true;
        if(bool == 0) return ""+false;
        return "BROKEN_BOOL";
    }
}
