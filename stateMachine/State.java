package stateMachine;

import scanner.Token;

/**
 * Created by Chris on 1/9/2016.
 */
public abstract class State {
    protected State nextState = this;
    protected StringBuilder sb = new StringBuilder();

    public abstract Token consume(char c);
    public abstract State nextState();

    public StringBuilder getSb() {
        return sb;
    }
}
