package testCases;

/**
 * Compilers
 *
 * @Author Carston
 * Date -  1/13/2016.
 */
public class FailedTestException extends RuntimeException {
    public FailedTestException(String message) {
        super(message);
    }
}
