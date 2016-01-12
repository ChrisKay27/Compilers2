package scanner;

import admininstration.Administration;
import parser.Tokens;
import stateMachine.State;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by Chris on 1/9/2016.
 */
public class Scanner {

    private final Reader reader;

    private ScannerStateMachine ssm = new ScannerStateMachine();
    private State state= ssm.init;

    private int lineCount = 1;
    private int colCount = 1;


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
            if(nextChar == '\n') {
                lineCount++;
                colCount = 0;
            }
            if (Administration.debug()) System.out.println("looking at char: " + (char)nextChar + "["+nextChar+"]");
            t = state.consume((char)nextChar);
            state = state.nextState();


            if(t == null) {
                if (Administration.debug()) System.out.println("Going to state:" + state);
                nextChar = nextChar();
                colCount++;
                if( nextChar == -1 ) {
                    if (state != ssm.init && state != ssm.lineComment)
                        throw new UnexpectedEndOfFileException("Unexpected End of file state="+state+" at line:" + lineCount + " col:" + colCount);
                    else
                        return new Token(Tokens.ENDFILE,null);
                }
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
