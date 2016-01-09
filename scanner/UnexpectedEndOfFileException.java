package scanner;

/**
 * Created by Chris on 1/9/2016.
 */
public class UnexpectedEndOfFileException extends RuntimeException {
    public UnexpectedEndOfFileException(String s) {
        super(s);
    }
}
