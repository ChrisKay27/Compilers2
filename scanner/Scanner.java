package scanner;

import stateMachine.State;
import stateMachine.Transition;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by Chris on 1/9/2016.
 */
public class Scanner {

    private final Reader reader;

    private final State state;




    public Scanner(Reader reader) throws IOException {
        this.reader = reader;

        ScannerStateMachine ssm = new ScannerStateMachine();

        state = ssm.init;


    }



    public Token nextToken() throws IOException {
        char nextChar = nextChar();

        Token t = state.consume(nextChar);


        return null;
    }




    private char nextChar() throws IOException {
        int r;
        if ((r = reader.read()) != -1)
            return (char) r;
        return (char) 0;
    }
}
