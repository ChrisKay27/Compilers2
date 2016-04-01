package parser.grammar.declarations;

import parser.Type;
import parser.grammar.ASTNode;
import scanner.Token;
import util.WTFException;

/**
 * Created by Chris on 1/30/2016.
 */
public class Declaration extends ASTNode {

    protected final Type type;
    protected final Token ID;
    protected int level = -1;
    protected int displacement;


    public Declaration(int line, Type type, Token ID) {
        super(line);
        this.type = type;
        this.ID = ID;
    }

    public Type getType() {
        return type;
    }

    public Token getID() {
        return ID;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);

        sb.append(tabsStr).append(getLine()).append(": ").append(getClass().getSimpleName());
        sb.append(tabsStr).append("\tid  : ").append(getID().getName());
        sb.append(tabsStr).append("\ttype: ").append(getType());

        if (nextNode != null)
            nextNode.appendContents(sb, tabs);
    }

    @Override
    public Declaration getNextNode() {
        return (Declaration) super.getNextNode();
    }

    public void setLevel(int level) { this.level = level; }

    public void setDisplacement(int displacement) { this.displacement = displacement; }

    public int getLevel() {
        return level;
    }

    public int getDisplacement() { return this.displacement; }

    public Declaration getLastNextNode() {
        Declaration d = getNextNode();
        if (d == null)
            return this;
        Declaration tmp = d.getNextNode();
        while (tmp != null) {
            d = tmp;
            tmp = tmp.getNextNode();
        }
        return d;
    }

    public int getLength() {
        if( type == Type.VOID )
            return 0;

        int length = 1;
        Declaration tmp = getNextNode();
        while (tmp != null) {
            tmp = tmp.getNextNode();
            length++;
        }
        return length;
    }

    public String getLD(){
        if( level == -1 )
            throw new WTFException("Level is -1 for this node! " + ID.getName());
        return "("+level+","+displacement+")";
    }

    public void setLevelAndDispl(int lvl, int displacement) {
        level = lvl;
        this.displacement = displacement;
    }
}
