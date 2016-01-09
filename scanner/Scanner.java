package scanner;

import stateMachine.State;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by Chris on 1/9/2016.
 */
public class Scanner {

    private final Reader reader;

    private State state;




    public Scanner(Reader reader) throws IOException {
        this.reader = reader;

        ScannerStateMachine ssm = new ScannerStateMachine();

        state = ssm.init;
    }

    private char nextChar;
    private boolean initNextChar = true;

    public Token nextToken() throws IOException {
        if(initNextChar){
            nextChar = nextChar();
            initNextChar = false;
        }

        Token t = null;
        while( t == null ){
            t = state.consume(nextChar);
            state = state.nextState();

            if(t == null)
                nextChar = nextChar();
        }

        return t;
    }




    private char nextChar() throws IOException {
        int r;
        if ((r = reader.read()) != -1)
            return (char) r;
        return (char) 0;
    }
}
