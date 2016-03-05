package util;

import java.io.FileNotFoundException;

/**
 * Created by chris_000 on 2/21/2016.
 */
public class WTFException extends RuntimeException{
    public WTFException() {

    }

    public WTFException(String message) {
        super(message);
    }

    public WTFException(Exception e) {
        super(e);
    }
}
