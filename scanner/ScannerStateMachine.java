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
            //System.out.println("Matching " + c);
            if (c <= 32) {
                nextState = init;
            } else {
                switch (c) {
                    case '{':
                        nextState = leftBrace;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '}':
                        nextState = rightBrace;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '[':
                        nextState = leftSquare;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case ']':
                        nextState = rightSquare;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '(':
                        nextState = leftParen;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case ')':
                        nextState = rightParen;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '+':
                        nextState = plus;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '-':
                        nextState = minus;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '<':
                        nextState = lessThanOrEq;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case ':':
                        nextState = colonEq;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '>':
                        nextState = greaterThanOrEq;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '|':
                        nextState = orElse;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '&':
                        nextState = andThen;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case '/':
                        nextState = divOrComment;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case ';':
                        nextState = semiColon;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;
                    case ',':
                        nextState = comma;
                        nextState.getSb().replace(0, nextState.getSb().length(), "");
                        nextState.getSb().append(c);
                        break;

                }
            }
            if (isDigit(c)) {
                nextState = num;
                nextState.getSb().replace(0, nextState.getSb().length(), "");
                nextState.getSb().append(c);
            } else if (isLetter(c)) {
                nextState = id;
                nextState.getSb().replace(0, nextState.getSb().length(), "");
                nextState.getSb().append(c);
            }
            return null;
        }

        @Override
        public String toString() {
            return "init state";
        }
    };


    State num = new State() {
        public Token consume(char c) {
            if (c >= 48 && c <= 57) {
                nextState = this;
                sb.append(c);
                return null;
            } else {
                nextState = init;
                String value = sb.toString();
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.NUM, value);
            }

        }

        public String toString() {
            return "num state";
        }
    };


    State id = new State() {
        public Token consume(char c) {
            if (isLetter(c)) {
                sb.append(c);
                nextState = this;
                return null;
            } else {
                String value = sb.toString();
                sb = new StringBuilder();
                return new Token(Tokens.ID, value);
            }
        }

        @Override
        public String toString() {
            return "id state";
        }
    };

    // remains in this state and ignores consumed characters until the end of line character is reached then returns
    // a null token that will be ignored by the parser
    State lineComment = new State() {
        boolean newLineFound = false;

        public Token consume(char c) {
            if (newLineFound) {
                newLineFound = false;
                return Token.COMMENT_TOKEN;
            } else if (Lexicon.isNewLine(c)) {
                newLineFound = true;
            } else {
                nextState = this;
            }
            return null;
        }

        @Override
        public String toString() {
            return "line comment state";
        }
    };
    // state could be a dash for negation or subtraction or could be a dash to start a line comment
    State minus = new State() {
        public Token consume(char c) {
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

        @Override
        public String toString() {
            return "minus state";
        }
    };

    State divOrComment = new State() {

        public Token consume(char c){
            if(c == '*' && sb.toString().equals("/")){
                nextState = blockComment;
                nextState.getSb().replace(0,nextState.getSb().length(),"");
                nextState.getSb().append("/*");
            }
            else{
                sb.replace(0,sb.length(),"");
                return new Token(Tokens.DIV,null);
            }

            return null;
        }

        @Override
        public String toString() {
            return "div or comment state";
        }
    };

    State lessThanOrEq = new State() {

        public Token consume(char c) {
            if (c == '=') {
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.LTEQ, null);
            } else {
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.LT, null);
            }
        }

        @Override
        public String toString() {
            return "< or = state";
        }
    };


    State greaterThanOrEq = new State() {

        public Token consume(char c) {
            if (c == '=') {
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.GTEQ, null);
            } else {
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.GT, null);
            }
        }

        @Override
        public String toString() {
            return "> = state";
        }
    };

    State orElse = new State() {

        public Token consume(char c) {

            if (c == '|') {

                sb.append('|');
                if (sb.toString().equals("||")) {
                    nextState = init;

                }

            } else if (sb.toString().equals("|")) {
                throw new BadTokenException("Bad Token: " + sb.toString() + c);
            }

            return null;
        }

        public State nextState() {
            return nextState;
        }

        @Override
        public String toString() {
            return "|| state";
        }
    };

    State colonEq = new State() {
        public Token consume(char c) {
            if (sb.toString().equals(":=")) {
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.ASSIGN, null);
            }

            if (c == '=') {
                sb.append('=');
            } else {
                return new Token(Tokens.EQ, null);
            }
            return null;
        }

        @Override
        public String toString() {
            return ":= state";
        }
    };


    State andThen = new State() {
        public Token consume(char c) {
            if (sb.toString().equals("&&")) {
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.ANDTHEN, null);
            }
            if (c == '&') {
                sb.append('&');
            } else {
                throw new BadTokenException("Bad Token: " + sb.toString() + c);
            }
            return null;
        }

        @Override
        public String toString() {
            return "&& state";
        }
    };

    // reached upon receiving '/*'
    // ignores all characters other than * and /
    // * -> goes to end comment state
    // / -> enters nestedComment state
    // OTHER -> loopback
    State blockComment = new State() {

        private int count;

        public Token consume(char c) {
            if (c == '*') {
                // potentially end of comment
                nextState = this.endComment;
            } else if (c == '/') {
                // potentially start of nested commend
                nextState = this.nestedComment;
            } else {
                // any other character inside a comment is ignored
                nextState = this;
            }
            return null;
        }
        public String toString() {
            return "blockComment state";
        }

        // reached upon receiving '/' from the block comment state
        // * -> increment the counter and return to block comment state
        // OTHER ignore and return to the block comment state
        private State nestedComment = new State() {

            public Token consume(char c) {
                if (c == '*') {
                    // go back to blockComment and return the comment token
                    increment();
                }
                nextState = blockComment;
                return null;
            }

            public String toString() {
                return "nestedComment state";
            }
        };

        // reached upon receiving '*' from the blockComment state
        // / -> end the comment and return the comment token
        // OTHER -> returns to blockComment state
        private State endComment = new State() {
            public Token consume(char c) {
                if (c == '/') {
                    if (countZero()) {
                        nextState = init;
                        return Token.COMMENT_TOKEN;
                    } else {
                        decrement();
                        nextState = blockComment;
                    }
                } else {
                    nextState = blockComment;
                }
                return null;
            }
            public String toString() {
                return "endComment state";
            }
        };

        private void increment() {
            count++;
        }

        private void decrement() {
            count--;
        }

        private boolean countZero() {
            return count == 0;
        }
    };


    State leftBrace = new State() {
        public Token consume(char c) {
            return new Token(Tokens.LCRLY, null);
        }
        @Override
        public String toString() {
            return "{ state";
        }
    };

    State rightBrace = new State(){
        public Token consume(char c){
            return new Token(Tokens.RCRLY,null);
        }
        @Override
        public String toString() {
            return "} state";
        }
    };

    State leftParen = new State() {
        public Token consume(char c) {
            return new Token(Tokens.LPAREN, null);
        }
        public String toString() {
            return "( state";
        }
    };
    State rightParen = new State() {
        public Token consume(char c) {
            return new Token(Tokens.RPAREN, null);
        }
        @Override
        public String toString() {
            return ") state";
        }
    };

    State plus = new State() {
        public Token consume(char c) {
            return new Token(Tokens.PLUS, null);
        }
        @Override
        public String toString() {
            return "+ state";
        }
    };
    State semiColon = new State() {
        public Token consume(char c) {
            return new Token(Tokens.SEMI, null);
        }
        public String toString() {
            return "; state";
        }
    };
    State comma = new State() {
        public Token consume(char c) {
            return new Token(Tokens.COMMA, null);
        }
        @Override
        public String toString() {
            return ", state";
        }
    };
    State leftSquare = new State() {
        public Token consume(char c) {
            return new Token(Tokens.LSQR, null);
        }
        public String toString() {
            return "[ state";
        }
    };
    State rightSquare = new State() {
        public Token consume(char c) {
            return new Token(Tokens.RSQR, null);
        }
        @Override
        public String toString() {
            return "] state";
        }
    };

}
