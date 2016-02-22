package parser.grammar.expressions;

public class BLIT_NidFactor extends NidFactor {
    private final int bool;

    public BLIT_NidFactor(int bool) {
        if(!(bool == 1 || bool == 0)) throw new RuntimeException("Bad Boolean Value :" + bool);
        this.bool = bool;
    }

    public int isBool() {
        return bool;
    }

    public String toString(){
        if(bool == 1) return ""+true;
        if(bool == 0) return ""+false;
        return "BROKEN_BOOL";
    }
}
