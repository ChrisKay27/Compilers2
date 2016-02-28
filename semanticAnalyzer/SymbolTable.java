package semanticAnalyzer;

import parser.grammar.ASTNode;
import util.WTFException;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Carston on 2/27/2016.
 */
public class SymbolTable {

    private ArrayList<SymbolTableEntry> index;
    private Stack<SymbolTableEntry> stack;
    private Stack<Integer> frameSizes;

    public SymbolTable() {
        index = new ArrayList<SymbolTableEntry>();
        stack = new Stack<SymbolTableEntry>();
        frameSizes = new Stack<Integer>();
        frameSizes.push(0);
    }

    public boolean isEmpty() {
        boolean indexEmpty = index.size() <= 0;
        boolean stackEmpty = stack.isEmpty();
        boolean frameSizesEmpty = frameSizes.peek() == 0;
        if (indexEmpty && stackEmpty && frameSizesEmpty) return true;
        else if (!indexEmpty && !stackEmpty && !frameSizesEmpty) return false;
        else {
            throw new WTFException("One of the underlying data structures is inconsistent [index: "
                    + indexEmpty + ", stack:" + stackEmpty + ", frames:" + frameSizesEmpty+"]");
        }
    }

    public SymbolTableEntry pop() {
        if (stack.empty()) return null;
        frameSizes.push(frameSizes.pop() - 1);
        SymbolTableEntry result = stack.pop();

        if (result.getPreviousOccurance() != null) {
            index.remove(result.getId());
            index.add(result.getId(), result.getPreviousOccurance());
        } else {
            index.remove(result.getId());
        }

        return result;
    }

    /**
     * @param entry
     * @return True if the index contained entry already, false if the index did not contain a previous occurrence of entry
     */
    public boolean push(SymbolTableEntry entry) {
        frameSizes.push(frameSizes.pop() + 1);
        if (index.size() <= entry.getId()) {
            index.add(entry);
            stack.push(entry);
            return false;
        } else {
            entry.setPreviousOccurance(index.remove(entry.getId()));
            index.add(entry.getId(), entry);
            stack.push(entry);
            return true;
        }
    }



    public SymbolTableEntry get(int id) {
        if (index.size() <= 0) return null;
        return index.get(id);
    }

    public void enterFrame() {
        frameSizes.push(0);
    }

    public void leaveFrame() {
        int count = frameSizes.peek();
        while (count > 1) {
            count = frameSizes.peek();
            this.pop();
        }
        int x = frameSizes.pop();
        if (x != 0) throw new WTFException("leave frame function screwed up. [ frameSize:" + x + "]");
    }

}
