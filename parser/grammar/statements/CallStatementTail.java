package parser.grammar.statements;

import parser.Type;
import parser.grammar.ASTNode;
import parser.grammar.declarations.FuncDeclaration;
import parser.grammar.expressions.Expression;

/**
 * Created by Chris on 2/12/2016.
 */
public class CallStatementTail extends StatementTail {

    private final Expression call_tail;
    private FuncDeclaration funcDecl;

    public CallStatementTail(int line, Expression call_tail) {
        super(line);
        this.call_tail = call_tail;
    }

    public ASTNode getCall_tail() {
        return call_tail;
    }

    @Override
    public void appendContents(StringBuilder sb, int tabs) {
        String tabsStr = '\n'+getTabs(tabs);
        sb.append(tabsStr).append("arguments: ");
        if( call_tail == null )
            sb.append(tabsStr).append("\tvoid");
        else
            call_tail.appendContents(sb,tabs+1);
    }

    public Type getType(){
        return call_tail.getType();
    }

    public void setFuncDecl(FuncDeclaration funcDecl) {
        this.funcDecl = funcDecl;
    }

    public FuncDeclaration getFuncDecl() {
        return funcDecl;
    }
}
