package parser.grammar.statements;

/**
 * Created by Chris on 2/12/2016.
 */
public abstract class StatementTail {
    public abstract void appendContents(StringBuilder sb, int tabs);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendContents(sb,0);
        return sb.toString();
    }
}
