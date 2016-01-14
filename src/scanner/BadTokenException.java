package scanner;

/**
 * Created by Chris on 1/9/2016.
 */
public class BadTokenException extends RuntimeException {
    public BadTokenException() {
    }

    public BadTokenException(String message) {
        super(message);
    }
}
