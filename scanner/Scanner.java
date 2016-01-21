package scanner;

import admininstration.Administration;
import parser.Tokens;
import stateMachine.State;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Chris on 1/9/2016.
 */
public class Scanner {

    private static HashMap<Object, Token> keywords = new HashMap<>();

    static {
        keywords.put("if", new Token(Tokens.IF, null));
        keywords.put("and", new Token(Tokens.AND, null));
        keywords.put("bool", new Token(Tokens.BOOL, null));
        keywords.put("branch", new Token(Tokens.BRANCH, null));
        keywords.put("case", new Token(Tokens.CASE, null));
        keywords.put("continue", new Token(Tokens.CONTINUE, null));
        keywords.put("default", new Token(Tokens.DEFAULT, null));
        keywords.put("else", new Token(Tokens.ELSE, null));
        keywords.put("end", new Token(Tokens.END, null));
        keywords.put("exit", new Token(Tokens.EXIT, null));
        keywords.put("int", new Token(Tokens.INT, null));
        keywords.put("loop", new Token(Tokens.LOOP, null));
        keywords.put("mod", new Token(Tokens.MOD, null));
        keywords.put("not", new Token(Tokens.NOT, null));
        keywords.put("or", new Token(Tokens.OR, null));
        keywords.put("ref", new Token(Tokens.REF, null));
        keywords.put("return", new Token(Tokens.RETURN, null));
        keywords.put("void", new Token(Tokens.VOID, null));
        keywords.put("true", new Token(Tokens.BLIT, 1));
        keywords.put("false", new Token(Tokens.BLIT, 0));
    }


    private Reader reader;
    private final Consumer<String> lineTraceOutput;
    private final Consumer<String> errorOutput;
    private final List<Token> tokensOnCurrentLine = new ArrayList<>();
    private final StringBuilder currentLine = new StringBuilder();

    private ScannerStateMachine ssm;

    private int lineCount = 1;
    private int colCount = 1;

    private Map<String, Integer> symbolTable = new HashMap<>();
    private String[] reverseSymbolTable = new String[1000000];
    private int symbolCount;
    private boolean traceEnabled;


    public Scanner(Reader reader, Consumer<String> lineTraceOutput, Consumer<String> errorOutput) throws IOException {
        this.reader = reader;

        this.lineTraceOutput = lineTraceOutput;

        this.errorOutput = errorOutput;
        ssm = new ScannerStateMachine(errorOutput);
    }

    public Reader redirectReader(Reader input) {

        Reader temp = this.reader;
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
    public Token nextToken() throws IOException {

        if (initNextChar) {
            nextChar = nextChar();
            initNextChar = false;
        }

        if (traceEnabled)
            //Add the character to the current line to be output during the line traceEnabled
            currentLine.append((char) nextChar);

        if (nextChar == -1)
            return new Token(Tokens.ENDFILE, null);

        if (Administration.debug()) System.out.println("Starting in init state");

        //Always starts in the start state
        State state = ssm.init;
        Token t = null;
        //Consume characters until we produce a token.
        while (t == null) {

            //Keeping track of the line number
            if (nextChar == '\n') {

                if (traceEnabled) {
                    //Line Trace Output
                    //if (!tokensOnCurrentLine.isEmpty()) {
                    lineTraceOutput.accept(lineCount + ":" + currentLine.toString().trim());
                    for (Token token : tokensOnCurrentLine)
                        lineTraceOutput.accept(lineCount + ":" + "\t\t" + token);
                    tokensOnCurrentLine.clear();
                    //} else
                    //   lineTraceOutput.accept(lineCount + ":");

                    currentLine.replace(0, currentLine.length(), "");
                }

                lineCount++;
                colCount = 0;
            }

            if (Administration.debug())
                System.out.println("looking at char: " + (char) nextChar + "[" + nextChar + "]");

            //let the current state consume the next character
            t = state.consume((char) nextChar);
            //get the next state that we must transition to
            state = state.nextState();

            if (Administration.debug()) System.out.println("went to " + state + " after char: " + (char) nextChar);


            //If the current state did not produce a token
            if (t == null) {
                //-1 indicates we have reached the end of the file
                if (nextChar == -1) {
                    //However if we are not in the correct states which allow the end of file to be reached, we throw an exception
                    if (!ssm.blockComment.countZero())
                        return new Token(Tokens.ERROR, "Unexpected end of file at line:" + lineCount + " col:" + colCount);
                    else { //else the end of file token is returned
                        t = state.consume(' ');
                        if( t == null )
                            t = new Token(Tokens.ENDFILE, null);
                    }
                }

                if (Administration.debug()) System.out.println("Going to state:" + state);
                //get the next character from the input
                nextChar = nextChar();
                //keep track of what column we are on
                colCount++;

                if (traceEnabled)
                    //Add the character to the current line to be output during the line traceEnabled
                    currentLine.append((char) nextChar);

            } else if (t == Token.COMMENT_TOKEN) {//If its a comment token we ignore it, resetting the state back to the init state
                t = null;
                state = ssm.init;
            }
        }

        if (t.token == Tokens.ID) { //If we have received an ID token then we now check to see if it is actually a keyword
            Token keyword = keywords.get(t.attrValue);
            if (keyword != null)
                t = keyword;
            else {//else the ID is not for a keyword, then we add it to the symbol table if it is not already there.
                String attrValue = (String) t.attrValue;
                if (!symbolTable.containsKey(attrValue))
                    symbolTable.put(attrValue, symbolCount++);

                //We then replace the ID value with a numeral which represents that string
                int id = symbolTable.get(attrValue);
                t.attrValue = id;
                reverseSymbolTable[id] = attrValue;
            }
        } else if (t.token == Tokens.ERROR) {//If its a error token we set which line and column it appeared on
            t.attrValue = t.attrValue + " at line:" + lineCount + " col:" + colCount;
        }

        if (Administration.debug()) System.out.println("Found Token:" + t);

        //So we don't append this character here because if we are returning a token we haven't consumed this character
        //so it should not be added to the current line yet

        if (traceEnabled) {
            if (currentLine.length() > 0) currentLine.deleteCharAt(currentLine.length() - 1);
            tokensOnCurrentLine.add(t);
        }

        return t;
    }


    private boolean EOFFound = false;
    private int nextChar() throws IOException {
        int r;
        if ((r = reader.read()) != -1)
            return r;
        if (EOFFound)
            return -1;
        EOFFound = true;
        return '\n';
    }

    public String getIdentifier(int id) {
        return reverseSymbolTable[id];
    }

    /**
     * @param message - string to be printed on System.err by the administrator
     */
    private void reportError(String message) {

    }

    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    public boolean isTraceEnabled() {
        return traceEnabled;
    }
}
