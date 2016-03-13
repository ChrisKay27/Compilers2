package semanticAnalyzer;

/**
 * Created by Carston on 2/27/2016.
 */
public class TestMain {

    public static void main(String[] args) {
        int counter = 0;
        SymbolTable table = new SymbolTable();
        table.enterFrame();
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.enterFrame();
        counter = 0;
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.enterFrame();
        counter = 0;
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.pop();
        table.push(new SymbolTableEntry(counter++, null));
        table.pop();
        System.out.println(table.push(new SymbolTableEntry(counter, null)));
        System.out.println(table.push(new SymbolTableEntry(counter, null)));
        table.push(new SymbolTableEntry(counter++, null));
        table.pop();
        table.leaveFrame();
        table.pop();
        table.push(new SymbolTableEntry(counter++, null));
        table.pop();
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.push(new SymbolTableEntry(counter++, null));
        table.pop();
        table.leaveFrame();
        table.leaveFrame();

        while (!table.isEmpty()) {
            System.out.println(table.pop());
        }
    }
}
