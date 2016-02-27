package semanticAnalyzer;

import parser.grammar.ASTNode;

/**
 * Created by Carston on 2/27/2016.
 */
public class SemanticAnalyzer {

    private ASTNode astRoot;
    private SymbolTable symbolTable;

    public SemanticAnalyzer(ASTNode astRoot){
        this.astRoot = astRoot;
        this.symbolTable = new SymbolTable();
    }
}
