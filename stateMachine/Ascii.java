package stateMachine;

/**
 * Created by Carston on 1/9/2016.
 */
public class Ascii {
    //
    //digits
    public static final int START_OF_DIGITS = 48;
    public static final int END_OF_DIGITS = 57;
    //letters
    public static final int START_OF_UPPERCASE = 65;
    public static final int END_OF_UPPERCASE = 90;
    public static final int START_OF_LOWERCASE = 97;
    public static final int END_OF_LOWERCASE = 122;
    //other
    public static final int LEFT_BRACE = 123;
    public static final int RIGHT_BRACE = 125;


    public static boolean isDigit(char c) {
        return (c >= START_OF_DIGITS && c <= END_OF_DIGITS);
    }

    public static boolean isLetter(char c) {
        return isUppercase(c) || isLowercase(c);
    }

    public static boolean isUppercase(char c) {
        return (c >= START_OF_UPPERCASE && c <= END_OF_UPPERCASE);
    }

    public static boolean isLowercase(char c) {
        return (c >= START_OF_LOWERCASE && c <= END_OF_LOWERCASE);
    }


}
