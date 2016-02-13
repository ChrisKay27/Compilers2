package parser.grammar.statements;

import parser.grammar.ASTNode;

/**
 * Created by Chris on 1/30/2016.
 */
public class Statement extends ASTNode {

    public static Statement nullStatement = new Statement();
    public static Statement exitStatement = new Statement();
    public static Statement continueStatement = new Statement();

    public Statement(){}
}
