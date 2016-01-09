package scanner;

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

        state = ssm.init;
        Token t = null;
        while( t == null ) {
            t = state.consume((char)nextChar);
            state = state.nextState();

            if(t == null) {
                nextChar = nextChar();
                if( nextChar == -1 )
                    throw new UnexpectedEndOfFileException("Unexpected End of file at line:" + lineCount + " col:" + colCount);
            }
        }

        return t;
    }




    private int nextChar() throws IOException {
        int r;
        if ((r = reader.read()) != -1)
            return r;
        return -1;
    }
}
