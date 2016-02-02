package parser.grammar;

/**
 * Created by Chris on 1/30/2016.
 */
public class AST {

    protected AST nextNode;



    public AST getNextNode() {
        return nextNode;
    }

    public void setNextNode(AST nextNode) {
        this.nextNode = nextNode;
    }
}
