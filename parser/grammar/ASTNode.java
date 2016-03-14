package parser.grammar;

/**
 * Created by Chris on 1/30/2016.
 */

public abstract class ASTNode {

    protected ASTNode nextNode;
    protected int line;

    protected String code = "";

    protected ASTNode(int line) {
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

    public static String getTabs(int tabs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tabs; i++)
            sb.append("    ");
        return sb.toString();
    }


    public int getLine() {
        return this.line;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public void appendCode(String append){
        code = code + '\n' + append;
    }
}
