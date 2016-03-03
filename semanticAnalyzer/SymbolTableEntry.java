package semanticAnalyzer;

import parser.grammar.ASTNode;

/**
 * Created by Carston on 2/27/2016.
 */
public class SymbolTableEntry {

    private int id;
    private int layer;
    private SymbolTableEntry previousOccurrence;
    private ASTNode node;

    public SymbolTableEntry(int id, ASTNode node) {
        this(id, null, node);
    }

    public SymbolTableEntry(int id, SymbolTableEntry previousOccurrence, ASTNode node) {
        this.id = id;
        this.previousOccurrence = previousOccurrence;
        this.node = node;
    }

    public int getId() {
        return id;
    }

    public SymbolTableEntry getPreviousOccurrence() {
        return previousOccurrence;
    }

    public void setPreviousOccurrence(SymbolTableEntry previousOccurrence) {
        this.previousOccurrence = previousOccurrence;
    }

    public ASTNode getNode() {
        return node;
    }

    public int getLayer() {return this.layer;}

    public void setLayer(int layer){
        this.layer = layer;
    }

    public boolean collision(SymbolTableEntry arg){
        return this.getLayer() == arg.getLayer();
    }

    public String toString(){
        return this.getId() + ":" + this.getPreviousOccurrence() + ":" + this.getNode();
    }
}
