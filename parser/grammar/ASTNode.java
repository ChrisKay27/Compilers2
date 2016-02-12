package parser.grammar;

/**
 * Created by Chris on 1/30/2016.
 */
public class ASTNode {

    protected ASTNode nextNode;



    public ASTNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(ASTNode nextNode) {
        this.nextNode = nextNode;
    }
}
