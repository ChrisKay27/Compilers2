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
            } else {
                switch (c) {
                    case '{':
                        nextState = leftBrace;
                        break;
                    case '}':
                        nextState = rightBrace;
                        break;
                    case '(':
                        nextState = leftParen;
                        break;
                    case ')':
                        nextState = rightParen;
                        break;
                    case '+':
                        nextState = plus;
                        break;
                    case '-':
                        nextState = minusOrLineComment;
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
                    case '/':
                        nextState = divOrComment;
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
    };


    State id = new State() {

        public Token consume(char c) {
            if (isLetter(c)) {
                sb.append(c);
                nextState = this;
                this.nextState();
            }
            String value = sb.toString();
            sb = new StringBuilder();
            return new Token(Tokens.ID, value);
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
    };

    State divOrComment = new State() {

        public Token consume(char c) {

            if (c == '/') {
                sb.replace(0, sb.length(), "");
                return new Token(Tokens.DIV, null);

            } else if (c == '*' && sb.toString().equals("/")) {

            } else {
                throw new BadTokenException("Bad Token: " + sb.toString() + c);
            }

            return null;
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

        public State nextState() {
            return nextState;
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

        public State nextState() {
            return nextState;
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
    };

    State colonEq = new State() {

        public Token consume(char c) {

            if (c == '=') {
                if (sb.toString().equals(":=")) {
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ASSIGN, null);
                }

                sb.append('=');
                if (sb.toString().equals("||")) {
                    nextState = init;
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ORELSE, null);
                }
            } else {

            }
            return null;
        }
    };

    State leftBrace = new State() {

        public Token consume(char c) {

            if (c == '=') {
                if (sb.toString().equals(":=")) {
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ASSIGN, null);
                }

                sb.append('=');
                if (sb.toString().equals("||")) {
                    nextState = init;
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ORELSE, null);
                }
            } else {

            }
            return null;
        }
    };
    State rightBrace = new State() {

        public Token consume(char c) {

            if (c == '=') {
                if (sb.toString().equals(":=")) {
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ASSIGN, null);
                }

                sb.append('=');
                if (sb.toString().equals("||")) {
                    nextState = init;
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ORELSE, null);
                }
            } else {

            }
            return null;
        }
    };
    State leftParen = new State() {

        public Token consume(char c) {

            if (c == '=') {
                if (sb.toString().equals(":=")) {
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ASSIGN, null);
                }

                sb.append('=');
                if (sb.toString().equals("||")) {
                    nextState = init;
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ORELSE, null);
                }
            } else {

            }
            return null;
        }
    };
    State plus = new State() {

        public Token consume(char c) {

            if (c == '=') {
                if (sb.toString().equals(":=")) {
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ASSIGN, null);
                }

                sb.append('=');
                if (sb.toString().equals("||")) {
                    nextState = init;
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ORELSE, null);
                }
            } else {

            }
            return null;
        }
    };
    State rightParen = new State() {

        public Token consume(char c) {

            if (c == '=') {
                if (sb.toString().equals(":=")) {
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ASSIGN, null);
                }

                sb.append('=');
                if (sb.toString().equals("||")) {
                    nextState = init;
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ORELSE, null);
                }
            } else {

            }
            return null;
        }
    };

    State minusOrLineComment = new State() {

        public Token consume(char c) {

            if (c == '=') {
                if (sb.toString().equals(":=")) {
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ASSIGN, null);
                }

                sb.append('=');
                if (sb.toString().equals("||")) {
                    nextState = init;
                    sb.replace(0, sb.length(), "");
                    return new Token(Tokens.ORELSE, null);
                }
            } else {

            }
            return null;
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

}
