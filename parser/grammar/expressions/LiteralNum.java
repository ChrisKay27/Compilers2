package parser.grammar.expressions;

public class LiteralNum extends NidFactor {
    private final int num;

    public LiteralNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public String toString(){
        return "" +this.num ;
    }
}
