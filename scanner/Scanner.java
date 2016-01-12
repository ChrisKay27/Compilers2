package scanner;

import admininstration.Administration;
import parser.Tokens;
import stateMachine.State;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

/**
 * Created by Chris on 1/9/2016.
 */
public class Scanner {

    private final Reader reader;

    private ScannerStateMachine ssm = new ScannerStateMachine();
    private State state= ssm.init;

    private int lineCount = 1;
    private int colCount = 1;
    private static HashMap<Object, Token> keywords = new HashMap<>();
    static{
        keywords.put("if",new Token(Tokens.IF,null));
        keywords.put("and",new Token(Tokens.AND,null));
        keywords.put("bool",new Token(Tokens.BOOL,null));
        keywords.put("branch",new Token(Tokens.BRANCH,null));
        keywords.put("case",new Token(Tokens.CASE,null));
        keywords.put("continue",new Token(Tokens.CONTINUE,null));
        keywords.put("default",new Token(Tokens.DEFAULT,null));
        keywords.put("else",new Token(Tokens.ELSE,null));
        keywords.put("end",new Token(Tokens.END,null));
        keywords.put("exit",new Token(Tokens.EXIT,null));
        keywords.put("int",new Token(Tokens.INT,null));
        keywords.put("loop",new Token(Tokens.LOOP,null));
        keywords.put("mod",new Token(Tokens.MOD,null));
        keywords.put("not",new Token(Tokens.NOT,null));
        keywords.put("or",new Token(Tokens.OR,null));
        keywords.put("ref",new Token(Tokens.REF,null));
        keywords.put("return",new Token(Tokens.RETURN,null));
        keywords.put("void",new Token(Tokens.VOID,null));
        keywords.put("true",new Token(Tokens.BOOL,1));
        keywords.put("false",new Token(Tokens.BOOL,0));
    }

    public Scanner(Reader reader) throws IOException {
        this.reader = reader;
    }

    private int nextChar;
    private boolean initNextChar = true;

    public Token nextToken() throws IOException {
        if(initNextChar){
            nextChar = nextChar();
            initNextChar = false;
        }
        if( nextChar == -1 )
            return new Token(Tokens.ENDFILE,null);

        if (Administration.debug()) System.out.println("Starting in init state");
        state = ssm.init;
        Token t = null;
        while( t == null ) {
            if( nextChar == -1 ) {
                if (state != ssm.init && state != ssm.lineComment)
                    throw new UnexpectedEndOfFileException("Unexpected End of file state="+state+" at line:" + lineCount + " col:" + colCount);
                else
                    return new Token(Tokens.ENDFILE,null);
            }
            if(nextChar == '\n') {
                lineCount++;
                colCount = 0;
            }
            t = state.consume((char)nextChar);
            state = state.nextState();
            if (Administration.debug()) System.out.println((char) nextChar + "[" + nextChar + "] -> '" + state + "'");

            if(t == null) {
                if (Administration.debug()) System.out.println("Going to state:" + state);
                nextChar = nextChar();
                colCount++;

            }
            else if( t.token == Tokens.ID ){
                Token keyword = keywords.get(t.attrValue);
                if( keyword != null )
                    t = keyword;
            }
            else if( t == Token.COMMENT_TOKEN ){
                t = null;
                state = ssm.init;
            }
        }
        if (Administration.debug()) System.out.println("Found Token:" + t);
        return t;
    }




    private int nextChar() throws IOException {
        int r;
        if ((r = reader.read()) != -1)
            return r;
        return -1;
    }
}
