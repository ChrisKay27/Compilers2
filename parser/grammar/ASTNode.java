package parser.grammar;

/**
 * Created by Chris on 1/30/2016.
 */
public abstract class ASTNode {

    protected ASTNode nextNode;
    protected String line;

    protected ASTNode(String line) {
        this.line = line;
    }

    public ASTNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(ASTNode nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendContents(sb, 0);
        return sb.toString();
    }

    public abstract void appendContents(StringBuilder sb, int tabs);

    public String getLine() {
        return line;
    }

    public static String getTabs(int tabs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        return sb.toString();
    }
}
