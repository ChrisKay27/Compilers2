package parser;

/**
 * Created by Chris on 1/30/2016.
 */
public class AbstractSyntaxTreeNode {

    protected AbstractSyntaxTreeNode nextNode;



    public AbstractSyntaxTreeNode getNextNode() {
        return nextNode;
    }

    public void setNextNode(AbstractSyntaxTreeNode nextNode) {
        this.nextNode = nextNode;
    }
}
