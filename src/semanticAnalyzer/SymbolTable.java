package semanticAnalyzer;

import parser.TokenType;
import parser.grammar.expressions.IdFactor;
import parser.grammar.expressions.LiteralBool;
import scanner.Token;
import util.WTFException;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by Carston on 2/27/2016.
 */
public class SymbolTable {

    private HashMap<Integer, SymbolTableEntry> index;
    private Stack<SymbolTableEntry> stack;
    private Stack<Integer> frameSizes;

    public SymbolTable() {
        index = new HashMap<Integer, SymbolTableEntry>();
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
                    + indexEmpty + ", stack:" + stackEmpty + ", frames:" + frameSizesEmpty + "]");
        }
    }

    public SymbolTableEntry pop() {
        if (stack.empty()) return null;
        frameSizes.push(frameSizes.pop() - 1);
        SymbolTableEntry result = stack.pop();

        if (result.getPreviousOccurrence() != null) {
            index.remove(result.getId());
            index.put(result.getId(), result.getPreviousOccurrence());
        } else {
            index.remove(result.getId());
        }

        return result;
    }

    public SymbolTableEntry peek() {
        if (stack.empty()) return null;
        return stack.peek();
    }

    /**
     * @return True if the entry could be inserted, false if there was a duplicate definition
     */
    public boolean push(SymbolTableEntry entry) {
        entry.setLayer(frameSizes.size());
        frameSizes.push(frameSizes.pop() + 1);
        if (index.containsKey(entry.getId())) {
            if (index.get(entry.getId()).collision(entry))
                return false;
            entry.setPreviousOccurrence(index.remove(entry.getId()));
            index.put(entry.getId(), entry);
            stack.push(entry);
            return true;
        } else {
            index.put(entry.getId(), entry);
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
        while (count > 0) {
            this.pop();
            count = frameSizes.peek();
        }
        int x = frameSizes.pop();
        if (x != 0) {
            this.index.forEach((k, v) -> System.out.println(k + ", " + v));
            this.stack.forEach(System.out::print);
            throw new WTFException("leave frame function screwed up. [frameSize: " + x + "]");
        }
    }
}