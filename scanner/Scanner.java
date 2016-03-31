package scanner;

import admininstration.Administrator;
import stateMachine.State;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.System.out;

/**
 * Created by Chris on 1/9/2016.
 */
public class Scanner {

    private static HashMap<Object, Token> keywords = new HashMap<>();

    static {
        keywords.put("if", new Token(TokenType.IF, null));
        keywords.put("and", new Token(TokenType.AND, null));
        keywords.put("bool", new Token(TokenType.BOOL, null));
        keywords.put("branch", new Token(TokenType.BRANCH, null));
        keywords.put("case", new Token(TokenType.CASE, null));
        keywords.put("continue", new Token(TokenType.CONTINUE, null));
        keywords.put("default", new Token(TokenType.DEFAULT, null));
        keywords.put("else", new Token(TokenType.ELSE, null));
        keywords.put("end", new Token(TokenType.END, null));
        keywords.put("exit", new Token(TokenType.EXIT, null));
        keywords.put("int", new Token(TokenType.INT, null));
        keywords.put("loop", new Token(TokenType.LOOP, null));
        keywords.put("mod", new Token(TokenType.MOD, null));
        keywords.put("not", new Token(TokenType.NOT, null));
        keywords.put("or", new Token(TokenType.OR, null));
        keywords.put("ref", new Token(TokenType.REF, null));
        keywords.put("return", new Token(TokenType.RETURN, null));
        keywords.put("void", new Token(TokenType.VOID, null));
        keywords.put("true", new Token(TokenType.BLIT, 1));
        keywords.put("false", new Token(TokenType.BLIT, 0));
    }

    private Supplier<Integer> reader;
    private final Consumer<List<Token>> tokensOnLineConsumer;
    private final Consumer<String> lineTraceOutput;
    private final Consumer<String> errorOutput;
    private final List<Token> tokensOnCurrentLine = new ArrayList<>();

    private ScannerStateMachine ssm;

    private int lineCount = 1;
    private int colCount = 1;

    private Map<String, Integer> symbolTable = new HashMap<>();
    private String[] reverseSymbolTable = new String[1000000];
    private int symbolCount;
    private boolean traceEnabled;


    public Scanner(Supplier<Integer> reader, Consumer<List<Token>> tokensOnLineConsumer, Consumer<String> lineTraceOutput, Consumer<String> errorOutput) throws IOException {
        this.reader = reader;
        this.tokensOnLineConsumer = tokensOnLineConsumer;
        this.lineTraceOutput = lineTraceOutput;
        this.errorOutput = errorOutput;

        symbolTable.put("readint", 0);
        symbolTable.put("writeint", 1);
        symbolTable.put("readbool", 2);
        symbolTable.put("writebool", 3);
        symbolTable.put("x", 4);

        reverseSymbolTable[0] = "readint";
        reverseSymbolTable[1] = "writeint";
        reverseSymbolTable[2] = "readbool";
        reverseSymbolTable[3] = "writebool";
        reverseSymbolTable[4] = "x";
        symbolCount = 5;

        ssm = new ScannerStateMachine(errorOutput);
    }

    public Supplier<Integer> redirectReader(Supplier<Integer> input) {

        Supplier<Integer> temp = this.reader;
        this.reader = input;
        this.initNextChar = true;
        this.lineCount = 1;
        this.colCount = 1;
        this.EOFFound = false;
        return temp;
    }

    private int nextChar;
    private boolean initNextChar = true;

    /**
     * Returns tokens in the file.
     *
     * @return Returns the next token in this file. If the end file token has been returned previously, it will continue
     * to be returned if this method is called again.
     * @throws IOException
     */
    public Token nextToken() {

        if (initNextChar) {
            nextChar = nextChar();
            initNextChar = false;
        }

//        if (traceEnabled)
//            //Add the character to the current line to be output during the line traceEnabled
//            currentLine.append((char) nextChar);

        if (nextChar == -1)
            return new Token(TokenType.ENDFILE, null);

        if (Administrator.debug()) out.println("Starting in init state");

        //Always starts in the start state
        State state = ssm.init;
        Token t = null;
        //Consume characters until we produce a token.
        while (t == null) {

            if (Administrator.debug())
                out.println("looking at char: " + (char) nextChar + "[" + nextChar + "]");

            //Keeping track of the line number
//            if (nextChar == '\n') {
//                tokensOnLineConsumer.accept(new ArrayList<>(tokensOnCurrentLine));
//                tokensOnCurrentLine.clear();
//                lineCount++;
//                colCount = 0;
//            }

            //let the current state consume the next character
            t = state.consume((char) nextChar);
            //get the next state that we must transition to
            state = state.nextState();

            if (Administrator.debug()) out.println("went to " + state + " after char: " + (char) nextChar);


            //If the current state did not produce a token
            if (t == null) {
                //-1 indicates we have reached the end of the file
                if (nextChar == -1) {
                    //However if we are not in the correct states which allow the end of file to be reached, we throw an exception
                    if (!ssm.blockComment.countZero())
                        return new Token(TokenType.ERROR, "Unexpected end of file at line:" + lineCount + " col:" + colCount);
                    else { //else the end of file token is returned
                        t = state.consume(' ');
                        if (t == null)
                            t = new Token(TokenType.ENDFILE, null);
                    }
                }

                if (Administrator.debug()) out.println("Going to state:" + state);
                //get the next character from the input
                nextChar = nextChar();
                //keep track of what column we are on
                colCount++;


//                if (traceEnabled)
//                    //Add the character to the current line to be output during the line traceEnabled
//                    currentLine.append((char) nextChar);

            } else if (t == Token.COMMENT_TOKEN) {//If its a comment token we ignore it, resetting the state back to the init state
                t = null;
                state = ssm.init;

            }
        }

        if (t.token == TokenType.ID) { //If we have received an ID token then we now check to see if it is actually a keyword
            Token keyword = keywords.get(t.name);
            if (keyword != null)
                t = keyword;
            else {//else the ID is not for a keyword, then we add it to the symbol table if it is not already there.
                String attrValue = t.name;
                if (!symbolTable.containsKey(attrValue))
                    symbolTable.put(attrValue, symbolCount++);

                //We then replace the ID value with a numeral which represents that string
                int id = symbolTable.get(attrValue);
//                t.attrValue = id;
                t.id = id;
                reverseSymbolTable[id] = attrValue;
            }
        } else if (t.token == TokenType.ERROR) {//If its a error token we set which line and column it appeared on
            t.name = t.name + " at line:" + lineCount + " col:" + colCount;
        }

        if (Administrator.debug()) out.println("Found Token:" + t);

        //So we don't append this character here because if we are returning a token we haven't consumed this character
        //so it should not be added to the current line yet

        //if (traceEnabled) {
        //    if (currentLine.length() > 0) currentLine.deleteCharAt(currentLine.length() - 1);
        tokensOnCurrentLine.add(t);
        // }

        lineTraceOutput.accept("\n\t\t" + t + '\n');

        if (nextChar == '\n') {
            tokensOnLineConsumer.accept(new ArrayList<>(tokensOnCurrentLine));
            tokensOnCurrentLine.clear();
            lineCount++;
            colCount = 0;
        }

        return t;
    }


    private boolean EOFFound = false;

    private int nextChar() {
        int r;
        if (EOFFound)
            return -1;
        if ((r = reader.get()) != -1)
            return r;
        EOFFound = true;
        return '\n';
    }

    public String getIdentifier(int id) {
        return reverseSymbolTable[id];
    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }
}
