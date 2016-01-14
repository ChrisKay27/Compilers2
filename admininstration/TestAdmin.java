package admininstration;

import scanner.Token;

import java.io.IOException;
import java.util.List;

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

    public boolean validateParse(List<Token> expectedTokens) {
        List receivedTokens = getParse();
        boolean passed = true;
        for (int i = 0; i < expectedTokens.size(); i++) {
            if( !(expectedTokens.get(i).equals(receivedTokens.get(i)))) {
                System.out.println(expectedTokens.get(i) + " /= " + receivedTokens.get(i));
                passed &= false;
            }
        }
        return passed;
    }

    public List<Token> getParse() {
        return parser.getTokenList();
    }
}
