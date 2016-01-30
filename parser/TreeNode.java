package parser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 1/25/2016.
 */
public class TreeNode {

    private List<TreeNode> children = new LinkedList<>();

    public int size() {
        return children.size();
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public boolean add(TreeNode treeNode) {
        return children.add(treeNode);
    }

    public boolean remove(Object o) {
        return children.remove(o);
    }
}
