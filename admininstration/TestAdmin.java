package admininstration;

import scanner.Token;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Compilers
 *
 * @Author Carston
 * Date -  1/13/2016.
 */
public class TestAdmin extends Administration {

    public TestAdmin(String path) throws IOException, UnrecognizedSourceCodeException {
        super(path);
    }

    public boolean validateParse(ArrayList<Token> expectedTokens) {
        return this.parser.getTokenString().equals(expectedTokens);
    }

    public ArrayList<Token> getParse() {
        return parser.getTokenString();
    }
}
