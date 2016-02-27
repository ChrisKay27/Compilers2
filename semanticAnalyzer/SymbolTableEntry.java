package semanticAnalyzer;

import parser.grammar.ASTNode;

/**
 * Created by Carston on 2/27/2016.
 */
public class SymbolTableEntry {

    private int id;
    private SymbolTableEntry previousOccurance;
    private ASTNode node;

    public SymbolTableEntry(int id, ASTNode node) {
        this(id, null, node);
    }

    public SymbolTableEntry(int id, SymbolTableEntry previousOccurance, ASTNode node) {
        this.id = id;
        this.previousOccurance = previousOccurance;
        this.node = node;
    }

    public int getId() {
        return id;
    }

    public SymbolTableEntry getPreviousOccurance() {
        return previousOccurance;
    }

    public void setPreviousOccurance(SymbolTableEntry previousOccurance) {
        this.previousOccurance = previousOccurance;
    }

    public ASTNode getNode() {
        return node;
    }

    public String toString(){
        return this.getId() + ":" + this.getPreviousOccurance() + ":" + this.getNode();
    }
}
