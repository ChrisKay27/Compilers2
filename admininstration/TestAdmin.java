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
public class TestAdmin extends Administrator {


    private Options options;

    public TestAdmin(Options options) throws IOException, UnrecognizedSourceCodeException {
        super(options);
        this.options = options;
        this.getOutputHandler().setMaxErrors(Integer.MAX_VALUE);//show all error messages that are generated during testing
    }

    public boolean validateParse(List<Token> expectedTokens) {
        List receivedTokens = getParse();
        boolean passed = true;
        for (int i = 0; i < expectedTokens.size() && i < receivedTokens.size(); i++) {
            if (!(expectedTokens.get(i).equals(receivedTokens.get(i)))) {
                if (options.verbose) System.out.println(expectedTokens.get(i) + " /= " + receivedTokens.get(i));
                passed &= false;
            }
        }
        return passed;
    }

    public List<Token> getParse() {
        return parser.getTokenList();
    }
}
