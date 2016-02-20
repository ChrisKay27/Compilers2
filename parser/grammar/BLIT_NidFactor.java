package parser.grammar;

import parser.grammar.expressions.NidFactor;

public class BLIT_NidFactor extends NidFactor {
    private final int bool;

    public BLIT_NidFactor(int bool) {
        this.bool = bool;
    }

    public int isBool() {
        return bool;
    }
}
