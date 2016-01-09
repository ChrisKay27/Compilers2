package scanner;

import parser.Tokens;
import stateMachine.Ascii;
import stateMachine.State;

import static stateMachine.Ascii.isDigit;
import static stateMachine.Ascii.isLetter;

/**
 * Created by Chris on 1/9/2016.
 */
public class ScannerStateMachine {

    State init = new State(){

        public Token consume(char c){
            if(c <= 32){
                nextState = init;
            }
            else if( c == Ascii.LEFT_BRACE ){

            }
            else if( isDigit(c) ){
                nextState = num;
                nextState.getSb().replace(0,nextState.getSb().length(),"");
                nextState.getSb().append(c);
            }
            else if( isLetter(c) ){
                nextState = id;
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

    State id = new State(){

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


        public Token consume(char c){
            return null;
        }

        public State nextState(){
            return this;
        }
    };

}
