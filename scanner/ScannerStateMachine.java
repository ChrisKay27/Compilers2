package scanner;

import parser.Tokens;
import stateMachine.Lexicon;
import stateMachine.State;

import static stateMachine.Lexicon.isDigit;
import static stateMachine.Lexicon.isLetter;

/**
 * Created by Chris on 1/9/2016.
 */
public class ScannerStateMachine {

    State init = new State() {

        public Token consume(char c) {
            if (c <= 32) {
                nextState = init;
            } else if (c == Lexicon.LEFT_BRACE) {

            }
            else if( isDigit(c) ){
                nextState = num;
                nextState.getSb().replace(0, nextState.getSb().length(), "");
                nextState.getSb().append(c);
            }
            else if( isLetter(c) ){
                nextState = id;
                nextState.getSb().replace(0,nextState.getSb().length(),"");
                nextState.getSb().append(c);
            } else if (c == Lexicon.MINUS) {
                nextState = minus;
                nextState.getSb().replace(0, nextState.getSb().length(), "");
                nextState.getSb().append(c);
            }
            return null;
        }

        public State nextState() {
            return nextState;
        }
    };


    State num = new State() {

        public Token consume(char c) {
            if (c >= 48 && c <= 57) {
                nextState = this;
                sb.append(c);
                return null;
            } else {
                String value = sb.toString();
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.NUM, value);
            }

        }

        public State nextState() {
            return this;
        }
    };

    State id = new State() {

        public Token consume(char c) {
            if (Lexicon.isLetter(c)) {
                sb.append(c);
                nextState = this;
                this.nextState();
            }
            String value = sb.toString();
            sb = new StringBuilder();
            return new Token(Tokens.ID, value);
        }

        public State nextState() {
            return nextState;
        }
    };

    // remains in this state and ignores consumed characters until the end of line character is reached then returns 
    // a null token that will be ignored by the parser
    State lineComment = new State() {

        public Token consume(char c) {
            if (Lexicon.isNewLine(c)) {
                return Token.COMMENT_TOKEN;
            } else {
                nextState = this;
                nextState();
            }
            return null;
        }

        public State nextState() {
            return nextState;
        }
    };
    // state could be a dash for negation or subtraction or could be a dash to start a line comment
    State minus = new State() {
        public Token consume(char c){
            if (c == Lexicon.MINUS) {
                //line comment
                nextState = lineComment;
                this.nextState();
            } else {
                // negation or subtraction
                String value = sb.toString();
                sb = new StringBuilder();
                return new Token(Tokens.MINUS, value);
            }
            return null;
        }
        public State nextState(){
            return nextState;
        }

    };

}
