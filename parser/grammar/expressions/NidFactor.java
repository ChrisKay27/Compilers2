package parser.grammar.expressions;

/**
 * Created by Chris on 2/12/2016.
 */
public abstract class NidFactor extends Factor {

    protected NidFactor(int line) {
        super(line);
    }

    @Override
    protected abstract int evaluateStaticInt();
}

