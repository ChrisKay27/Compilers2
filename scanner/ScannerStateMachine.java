package scanner;

import stateMachine.State;

import java.util.function.Consumer;

import static stateMachine.Lexicon.*;

/**
 *
 * Created by Chris on 1/9/2016.
 */
public class ScannerStateMachine {

    private final Consumer<String> errorOutput;

    public ScannerStateMachine(Consumer<String> errorOutput) {
        this.errorOutput = errorOutput;
    }

    /**
     * Reached when after returning a token or when the state machine is initialized
     */
    final State init = new State() {

        public Token consume(char c) {

            nextState = init;
            if (c == (char) -1) { //added end of file case in the state machine
                return new Token(TokenType.ENDFILE, null);
            } else if (c <= 32) {
                nextState = init;
            } else if (isDigit(c)) {
                nextState = num;
                nextState.clearAndAppend(c);
            } else if (isLetter(c)) {
                nextState = id;
                nextState.getSb().replace(0, nextState.getSb().length(), "");
                nextState.clearAndAppend(c);
            } else {
                switch (c) {
                    case '=': nextState = equal; break;
                    case '{':
                        nextState = leftBrace;
                        break;
                    case '}':
                        nextState = rightBrace;
                        break;
                    case '[':
                        nextState = leftSquare;
                        break;
                    case ']':
                        nextState = rightSquare;
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
                        nextState = minus;
                        break;
                    case '*':
                        nextState = multiply;

                        nextState.clearAndAppend(c);
                        break;
                    case '<':
                        nextState = lessThanOrEq;
                        //Clears the string builder at the next state and adds
                        //this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    case ':':
                        nextState = colonEq;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    case '>':
                        nextState = greaterThanOrEq;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    case '|':
                        nextState = orElse;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    case '&':
                        nextState = andThen;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    case '/':
                        nextState = divOrComment;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    case ';':
                        nextState = semiColon;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    case ',':
                        nextState = comma;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        break;
                    default: // report the following illegitimate characters: !"#$%\'.?@^_`
                        nextState = error;
                        //Clears the string builder at the next state
                        //Adds this character into that states string builder
                        nextState.clearAndAppend(c);
                        //errorOutput.accept("Error - Illegitimate character " + c + " found.");
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "init state";
        }
    };

    /**
     * Reached when an illegal character is read
     * Returns an UNIV token with value equal to the string so far
     */
    final State error = new State() {
        public Token consume(char c) {
            String badChar = sb.toString();
            return new Token(TokenType.ERROR, badChar);
        }

        public String toString() {
            return "error state";
        }
    };

    /**
     * Deals with reading numbers and producing NUM tokens
     *
     * Reached upon receiving a digit in the init state
     * Stays in this state as long as digits are received
     * Otherwise returns a NUM token with value equal to the string so far
     */
    final State num = new State() {
        public Token consume(char c) {
            if (c >= 48 && c <= 57) {
                nextState = this;
                sb.append(c);
                return null;
            } else {
                nextState = init;
                String value = sb.toString();
                sb = new StringBuilder();
                int number;
                try{
                    number = Integer.parseInt(value);
                } catch(java.lang.NumberFormatException e){
                    return new Token(TokenType.ERROR, "Number format exception " + e.getMessage());
                }
                return new Token(TokenType.NUM, number);
            }

        }

        public String toString() {
            return "num state";
        }
    };

    /**
     * Deals with reading valid ID characters and producing ID tokens.
     *
     * Reached upon receiving a letter in the init state
     * Stays in this state as long as letters, digits, $ or _ are received
     * Otherwise returns an ID token with value equal to the string so far
     */
    final State id = new State() {
        public Token consume(char c) {
            if (isIdCharacter(c)) {
                sb.append(c);
                nextState = this;
                return null;
            } else {
                String value = sb.toString();
                sb = new StringBuilder();
                return new Token(TokenType.ID, value);
            }
        }

        @Override
        public String toString() {
            return "id state";
        }
    };

    /**
     * Reached upon receiving '--' from the minus state
     * Remains in this state and ignores consumed characters until the end of line character is reached
     * Returns a COMMENT token that will be ignored by the parser
     */
    final State lineComment = new State() {
        boolean newLineFound = false;

        public Token consume(char c) {
            if (newLineFound) {
                newLineFound = false;
                return Token.COMMENT_TOKEN;
            } else if (isNewLine(c)) {
                newLineFound = true;
                nextState = this;
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

    /**
     * State could be a dash for negation or subtraction or could be a dash to start a line comment
     *
     * Reached after receiving '-' in the init state
     * If a '-' is received, go to the line comment state
     * Otherwise returns a MINUS token
     */
    final State minus = new State() {
        public Token consume(char c) {
            if (c == '-') {
                //line comment
                nextState = lineComment;
            } else {
                // negation or subtraction
                return new Token(TokenType.MINUS, null);
            }
            return null;
        }

        @Override
        public String toString() {
            return "minus state";
        }
    };

    final State multiply = new State() {

        public Token consume(char c) {
            return new Token(TokenType.MULT, null);
        }

        @Override
        public String toString() {
            return "multiply state";
        }
    };

    /**
     *  Reached upon receiving a '/' in the init state
     *  If a '*' is received, goes to the blockComment state
     *  If a '=' is received goes to the neq state
     *  Otherwise returns a DIV token
     */
    final State divOrComment = new State() {

        public Token consume(char c) {
            if (sb.toString().equals("/") && c == '*') {
                sb = new StringBuilder();
                nextState = blockComment;
                blockComment.count = 1;
                nextState.getSb().replace(0, nextState.getSb().length(), "");
                nextState.clearAndAppend("/*");
            } else if (sb.toString().equals("/") && c == '=') {
                sb = new StringBuilder();
                nextState = neq;
                //nextState.getSb().replace(0,nextState.getSb().length(),"");
                //nextState.getSb().append("/=");
            } else {
                sb.replace(0, sb.length(), "");
                return new Token(TokenType.DIV, null);
            }

            return null;
        }

        @Override
        public String toString() {
            return "div or comment state";
        }
    };
    /**
     * Reached after receiving a '/=' from the divOrComment state
     * Returns a NEQ token
     */
    State neq = new State() {

        public Token consume(char c) {
            return new Token(TokenType.NEQ, null);
        }

        @Override
        public String toString() {
            return "/= state";
        }
    };
    /**
     * Reached after receiving a '<' from the init state
     * If a '=' is received, return to this state
     * if the string so far is '<=' return a LTEQ token
     * Otherwise return a LT token
     */
	final State lessThanOrEq = new State() {

        public Token consume(char c) {
            if( sb.toString().equals("<=") ){
                return new Token(TokenType.LTEQ, null);
            }

            if (c == '=') {
                sb.append('=');
            } else {
                sb.replace(0, sb.length(), "");
                return new Token(TokenType.LT, null);
            }
            return null;
        }

        @Override
        public String toString() {
            return "< or = state";
        }
    };


    /**
     * Reached after receiving a '>' from the init state
     * If a '=' is received, return to this state
     * if the string so far is '>=' return a GTEQ token
     * Otherwise return a GT token
     */
    State greaterThanOrEq = new State() {

        public Token consume(char c) {
            if( sb.toString().equals(">=") ){
                return new Token(TokenType.GTEQ, null);
            }

            if (c == '=') {
                sb.append('=');
            } else {
                sb.replace(0, sb.length(), "");
                return new Token(TokenType.GT, null);
            }
            return null;
        }

        @Override
        public String toString() {
            return "> = state";
        }
    };

    /**
     * Reached after receiving a '|' from the init state
     * If a '|' is received, return to this state
     * if the string so far is '||' return a ORLESE token
     * Otherwise return an UNIV token
     */
	final State orElse = new State() {

        public Token consume(char c) {
            if (sb.toString().equals("||")) {
                sb = new StringBuilder();
                return new Token(TokenType.ORELSE,null);
            }
            if (c == '|') {
                sb.append('|');
            } else if (sb.toString().equals("|")) {
                String err = sb.toString();
                sb = new StringBuilder();
                return new Token(TokenType.ERROR, err);
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


    /**
     * Reached after receiving a ':' from the init state
     * If a '=' is received, return to this state
     * if the string so far is ':=' return a ASSIGN token
     * Otherwise return an UNIV token
     */
	final State colonEq = new State() {
        public Token consume(char c) {
            if (sb.toString().equals(":=")) {
                sb.replace(0, sb.length(), "");
                return new Token(TokenType.ASSIGN, null);
            }
            if (c == '=') {
                sb.append('=');
            } else {
                sb = new StringBuilder();
                return new Token(TokenType.COLON, null);
            }
            return null;
        }

        @Override
        public String toString() {
            return ":= state";
        }
    };

     /**
     * Reached after receiving a '&' from the init state
     * If a '&' is received, return to this state
     * if the string so far is '&&' return a ANDTHEN token
     * Otherwise return an UNIV token
     */
	final State andThen = new State() {
        public Token consume(char c) {
            //If this state has the string && built up then we clear its string and return the ANDTHEN token
            if (sb.toString().equals("&&")) {
                sb = new StringBuilder();
                return new Token(TokenType.ANDTHEN, null);
            }
            //else it should only have the string &, we then append the next & symbol to it.
            if (c == '&') {
                sb.append('&');
            } else {
                String err = sb.toString();
                sb = new StringBuilder();
                return new Token(TokenType.ERROR, err);
            }
            return null;
        }

        @Override
        public String toString() {
            return "&& state";
        }
    };


    /**
     * Reached upon receiving '/*'
     * ignores all characters other than * and /
     * * -> goes to end comment state
     * / -> enters nestedComment state
     * OTHER -> loopback
     */
    BlockCommentState blockComment = new BlockCommentState();


    public class BlockCommentState extends State {
        private int count;

        public Token consume(char c) {
            if (c == '*') {
                // potentially end of comment
                //endComment.getSb().append('*');
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

        /**
         * reached upon receiving '/' from the block comment state
         * * -> increment the counter and return to block comment state
         * OTHER ignore and return to the block comment state
         */
        private State nestedComment = new State() {

            public Token consume(char c) {
                if (c == '*') {
                    // go back to blockComment and return the comment token
                    increment();
                }
                nextState = blockComment;
                return null;
            }

            @Override
            public String toString() {
                return "nestedComment state";
            }
        };

        /**
         * reached upon receiving '*' from the blockComment state. The * will
         * / -> end the comment and return the comment token
         * OTHER -> returns to blockComment state
         */
        private State endComment = new State() {
            public Token consume(char c) {

                if (c == '/') {
                    if (count == 1) {
                        nextState = end;

                    } else {
                        decrement();
                        sb = new StringBuilder();
                        nextState = blockComment;
                    }
                } else {
                    sb = new StringBuilder();
                    nextState = blockComment;
                }
                return null;
            }

            @Override
            public String toString() {
                return "endComment state";
            }
        };

        /**
         * Upon receiving the last * / control is passed to this state, which returns the comment token.
         */
        private State end = new State() {
            public Token consume(char c) {
                return Token.COMMENT_TOKEN;
            }

            @Override
            public String toString() {
                return "endComment state";
            }
        };

        private void increment() {
            //System.out.println("inc count");
            count++;
        }

        private void decrement() {
            //System.out.println("dec count");
            count--;
        }

        boolean countZero() {
            return count == 0;
        }
    }


    /**
     * Reached from init after recieving '{'
     * Returns a LCRLY token.
     */
	final State leftBrace = new State() {
        public Token consume(char c) {
            return new Token(TokenType.LCRLY, null);
        }

        @Override
        public String toString() {
            return "{ state";
        }
    };

    /**
     * Reached from init after recieving '}'
     * Returns a RCRLY token.
     */
	final State rightBrace = new State() {
        public Token consume(char c) {
            return new Token(TokenType.RCRLY, null);
        }

        @Override
        public String toString() {
            return "} state";
        }
    };

    /**
     * Reached from init after receiving '('
     * Returns a LPAREN token.
     */
	final State leftParen = new State() {
        public Token consume(char c) {
            return new Token(TokenType.LPAREN, null);
        }

        public String toString() {
            return "( state";
        }
    };

    /**
     * Reached from init after receiving ')'
     * Returns a RPAREN token.
     */
	final State rightParen = new State() {
        public Token consume(char c) {
            return new Token(TokenType.RPAREN, null);
        }

        @Override
        public String toString() {
            return ") state";
        }
    };

    /**
     * Reached from init after receiving '['
     * Returns a LSQR token.
     */
	final State leftSquare = new State() {
        public Token consume(char c) {
            return new Token(TokenType.LSQR, null);
        }

        public String toString() {
            return "[ state";
        }
    };

    /**
     * Reached from init after receiving ']'
     * Returns a RSQR token.
     */
	final State rightSquare = new State() {
        public Token consume(char c) {
            return new Token(TokenType.RSQR, null);
        }

        @Override
        public String toString() {
            return "] state";
        }
    };

    /**
     * Reached from init after receiving '+'
     * Returns a PLUS token.
     */
	final State plus = new State() {
        public Token consume(char c) {
            return new Token(TokenType.PLUS, null);
        }

        @Override
        public String toString() {
            return "+ state";
        }
    };

    /**
     * Reached from init after receiving '='
     * Returns a EQ token.
     */
	final State equal = new State() {
        public Token consume(char c) {
            return new Token(TokenType.EQ, null);
        }

        public String toString() {
            return "= equal";
        }
    };

    /**
     * Reached from init after receiving ';'
     * Returns a SEMI token.
     */
	final State semiColon = new State() {
        public Token consume(char c) {
            return new Token(TokenType.SEMI, null);
        }

        public String toString() {
            return "; state";
        }
    };

    /**
     * Reached from init after receiving ','
     * Returns a COMMA token.
     */
	final State comma = new State() {
        public Token consume(char c) {
            return new Token(TokenType.COMMA, null);
        }

        @Override
        public String toString() {
            return ", state";
        }
    };


}
