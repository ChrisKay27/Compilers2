package scanner;

import stateMachine.Ascii;
import stateMachine.State;

/**
 * Created by Chris on 1/9/2016.
 */
public class ScannerStateMachine {

    State init = new State(){

        public Token consume(char c){
            if(c <= 32){
                nextState = init;
            }
            else if( c >= 33 && c <= 47 ){

            }
            else if( c >= 48 && c <= 57 ){
                nextState = num;
                nextState.getSb().replace(0,nextState.getSb().length(),"");
                nextState.getSb().append(c);

            }

            return null;
        }

        public State nextState(){
            return nextState;
        }
    };


    State num = new State(){

        public Token consume(char c){
            if( c >= 48 && c <= 57 ){
                nextState = this;
                sb.append(c);
                return null;
            }
            else{
                String value = sb.toString();
                sb.replace(0,sb.length(),"");
                return new Token(Tokens.NUM,value);
            }

        }

        public State nextState(){
            return this;
        }
    };


    State letter = new State(){

        public Token consume(char c){
            if (Ascii.isLetter(c)){
                    return null;
            }
            return null;
        }
        public State nextState(){
            return nextState;
        }
    };



}
