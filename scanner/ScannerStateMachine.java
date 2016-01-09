package scanner;

import stateMachine.State;

/**
 * Created by Chris on 1/9/2016.
 */
public class ScannerStateMachine {

    State init = new State(){

        public Token consume(char c){
            if( c >= 48 && c <= 58 ){
                nextState = num;
                return null;
            }
            return null;
        }
        public State nextState(){
            return nextState;
        }
    };


    State num = new State(){

        public Token consume(char c){
            if( c >= 48 && c <= 58 ){
                nextState = this;
                return null;
            }
            return null;
        }
        public State nextState(){
            return this;
        }
    };

}
