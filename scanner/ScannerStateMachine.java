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

    State init = new State(){

        public Token consume(char c){
            //System.out.println("Matching " + c);
            if(c <= 32){
                nextState = init;
            }
            else {
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


    State num = new State(){
        public Token consume(char c){
            if( c >= 48 && c <= 57 ){
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

        public State nextState(){
            return nextState;
        }
    };


    State id = new State() {
        public Token consume(char c) {
            if (isLetter(c)) {
                sb.append(c);
                nextState = this;
                return null;
            }
            else {
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
            if( newLineFound ) {
                newLineFound = false;
                return Token.COMMENT_TOKEN;
            }else if (Lexicon.isNewLine(c)) {
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

    State orElse = new State(){

        public Token consume(char c){

            if(c == '|'){

                sb.append('|');
                if(sb.toString().equals("||")){
                    nextState = init;

                }

            }else if(sb.toString().equals("|")){
                throw new BadTokenException("Bad Token: " + sb.toString() + c);
            }

            return null;
        }

        public State nextState(){
            return nextState;
        }
    };


    State andThen = new State(){
        public Token consume(char c){
            if( sb.toString().equals("&&") ){
                sb.replace(0,sb.length(),"");
                return new Token(Tokens.ANDTHEN,null);
            }
            if(c == '&'){
                sb.append('&');
            }else{
                throw new BadTokenException("Bad Token: " + sb.toString() + c);
            }
            return null;
        }
        @Override
        public String toString() {
            return "&& state";
        }
    };

    State leftBrace = new State(){

        public Token consume(char c){

            if(c == '='){
                if( sb.toString().equals(":=") ){
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ASSIGN,null);
                }

                sb.append('=');
                if(sb.toString().equals("||")){
                    nextState = init;
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ORELSE,null);
                }
            }else{

            }
            return null;
        }
        @Override
        public String toString() {
            return ":= state";
        }
    };
    State rightBrace = new State(){

        public Token consume(char c){

            if(c == '='){
                if( sb.toString().equals(":=") ){
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ASSIGN,null);
                }

                sb.append('=');
                if(sb.toString().equals("||")){
                    nextState = init;
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ORELSE,null);
                }
            }else{

            }
            return null;
        }
    };
    State leftParen = new State(){

        public Token consume(char c){

            if(c == '='){
                if( sb.toString().equals(":=") ){
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ASSIGN,null);
                }

                sb.append('=');
                if(sb.toString().equals("||")){
                    nextState = init;
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ORELSE,null);
                }
            }else{

            }
            return null;
        }
    };
    State plus = new State(){

        public Token consume(char c){

            if(c == '='){
                if( sb.toString().equals(":=") ){
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ASSIGN,null);
                }

                sb.append('=');
                if(sb.toString().equals("||")){
                    nextState = init;
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ORELSE,null);
                }
            }else{

            }
            return null;
        }
    };
    State rightParen = new State(){

        public Token consume(char c){

            if(c == '='){
                if( sb.toString().equals(":=") ){
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ASSIGN,null);
                }

                sb.append('=');
                if(sb.toString().equals("||")){
                    nextState = init;
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ORELSE,null);
                }
            }else{

            }
            return null;
        }
    };

    State minusOrLineComment = new State(){

        public Token consume(char c){

            if(c == '='){
                if( sb.toString().equals(":=") ){
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ASSIGN,null);
                }

                sb.append('=');
                if(sb.toString().equals("||")){
                    nextState = init;
                    sb.replace(0,sb.length(),"");
                    return new Token(Tokens.ORELSE,null);
                }
            }else{

            }
            return null;
        }
    };
    State semiColon = new State(){
        public Token consume(char c){
            return new Token(Tokens.SEMI,null);
        }
    };
    State comma = new State(){
        public Token consume(char c){
            return new Token(Tokens.COMMA,null);
        }
    };
    State leftSquare = new State(){
        public Token consume(char c){
            return new Token(Tokens.LSQR,null);
        }
    };
    State rightSquare = new State(){
        public Token consume(char c){
            return new Token(Tokens.RSQR,null);
        }
    };


}
