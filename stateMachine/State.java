package stateMachine;

import scanner.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 1/9/2016.
 */
public abstract class State {
    protected State nextState;
    public abstract Token consume(char c);
    public abstract State nextState();
}
