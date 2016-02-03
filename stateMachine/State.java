package stateMachine;

import scanner.Token;

/**
 * Created by Chris on 1/9/2016.
 */
public abstract class State {
    protected State nextState = this;

    /**
     * This string builder is used to build up the current input tokens value. i.e., If we are making an id token then
     * this stores the characters that make up that identifier. If we are in the NUM state then this stores the integer
     * characters.
     *
     * When we leave these states we return a token which is the toString of this StringBuilder
     */
    protected StringBuilder sb = new StringBuilder();

    public abstract Token consume(char c);
    public State nextState(){
        return nextState;
    }

    public StringBuilder getSb() {
        return sb;
    }

    public abstract String toString();

    public void clearAndAppend(String s) {
        sb = new StringBuilder();
        sb.append(s);
    }
    public void clearAndAppend(char c) {
        sb = new StringBuilder();
        sb.append(c);
    }
}
