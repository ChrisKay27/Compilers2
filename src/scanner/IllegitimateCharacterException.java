package scanner;

/**
 * Created by Chris on 1/9/2016.
 */
public class IllegitimateCharacterException extends RuntimeException {
    public IllegitimateCharacterException() {
    }

    public IllegitimateCharacterException(String message) {
        super(message);
    }
}
