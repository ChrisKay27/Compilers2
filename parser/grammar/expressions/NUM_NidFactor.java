package parser.grammar.expressions;

import parser.grammar.expressions.NidFactor;

public class NUM_NidFactor extends NidFactor {
    private final int num;

    public NUM_NidFactor(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
    public String toString(){
        return "" +this.num ;
    }
}
