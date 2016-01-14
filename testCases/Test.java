package testCases;

import admininstration.TestAdmin;
import parser.Tokens;
import scanner.Token;

import java.util.*;

/**
 * Concept test cases class, perhaps how the test cases are structured needs some work
 * but the idea that there is a class that handles the testing will be useful.
 *
 * @Author Carston
 * Date -  1/12/2016.
 */
public class Test {
    //
    private static final String TEST_CASE_PATH = "testCases/";
    //
    protected List<Token> expectedTokens;
    protected String name;
    private TestAdmin admin;

    //
    public Test(String name, List<Token> expectedTokens) {
        this.name = name;
        this.expectedTokens = expectedTokens;
    }

    public boolean run() {
        try {
            admin = new TestAdmin(TEST_CASE_PATH + this.name);
            admin.compile();
            admin.close();
            if (admin.validateParse(expectedTokens)) return true;
            /*else throw new FailedTestException(
                    "Test " + name + " has failed./n"
                            + expectedTokens.toString() + "/n"
                            + admin.getParse().toString() + "/n"

            );*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean runAll() {
        boolean res = true;
        for (Test test : getTestCases().values()) {
            //String name = test.getKey();
            boolean success = test.run();
            if (!success) {
                System.out.println("Expected Tokens:" + test.expectedTokens);
                System.out.println("Received Tokens:" + test.admin.getParse());
            }

            res &= success;

        }
        return res;
    }


    public static HashMap<String, Test> getTestCases() {
        HashMap<String, Test> testCases = new HashMap<>();

        List<Token> standardEnvironmentTokens = Arrays.asList(
                //int readint(void);
                new Token(Tokens.INT, null), new Token(Tokens.ID, 0), new Token(Tokens.LPAREN, null), new Token(Tokens.VOID, null), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                //void writeint(int x);
                new Token(Tokens.VOID, null), new Token(Tokens.ID, 1), new Token(Tokens.LPAREN, null), new Token(Tokens.INT, null), new Token(Tokens.ID, 2), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                //bool readbool(void);
                new Token(Tokens.BOOL, null), new Token(Tokens.ID, 3), new Token(Tokens.LPAREN, null), new Token(Tokens.VOID, null), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                //void writebool(bool x);
                new Token(Tokens.VOID, null), new Token(Tokens.ID, 4), new Token(Tokens.LPAREN, null), new Token(Tokens.BOOL, null), new Token(Tokens.ID, 2), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null)
        );

        String fileName = "simple.cs16";
        List<Token> expectedTokens = new ArrayList<>(standardEnvironmentTokens);
        expectedTokens.addAll(Arrays.asList(new Token(Tokens.IF, null), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 5),
                new Token(Tokens.GT, null), new Token(Tokens.NUM, 0), new Token(Tokens.RPAREN, null), new Token(Tokens.ID, 6), new Token(Tokens.ASSIGN, null),
                new Token(Tokens.ID, 5), new Token(Tokens.PLUS, null), new Token(Tokens.NUM, 1), new Token(Tokens.SEMI, null), new Token(Tokens.ENDFILE, null)));
        testCases.put(fileName, new Test(fileName, expectedTokens));

        fileName = "input.cs16";
        expectedTokens = new ArrayList<>(standardEnvironmentTokens);
        expectedTokens.addAll(Arrays.asList(new Token(Tokens.INT, null), new Token(Tokens.ID, 5), new Token(Tokens.ASSIGN, null),
                new Token(Tokens.NUM, 0), new Token(Tokens.SEMI, null), new Token(Tokens.INT, null), new Token(Tokens.ID, 6), new Token(Tokens.ASSIGN, null),
                new Token(Tokens.NUM, 0), new Token(Tokens.SEMI, null), new Token(Tokens.ID, 7), new Token(Tokens.ID, 8), new Token(Tokens.VOID, null), new Token(Tokens.ID, 9),
                new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 10), new Token(Tokens.LSQR, null), new Token(Tokens.RSQR, null), new Token(Tokens.ID, 11),
                new Token(Tokens.RPAREN, null), new Token(Tokens.LCRLY, null), new Token(Tokens.ID, 12), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 5),
                new Token(Tokens.PLUS, null), new Token(Tokens.ID, 6), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null), new Token(Tokens.ID, 12),
                new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 5), new Token(Tokens.LSQR, null), new Token(Tokens.RSQR, null), new Token(Tokens.PLUS, null),
                new Token(Tokens.ID, 6), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null), new Token(Tokens.IF, null),
                new Token(Tokens.LPAREN, null), new Token(Tokens.BLIT, 1), new Token(Tokens.RPAREN, null), new Token(Tokens.LCRLY, null),
                new Token(Tokens.ID, 12), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 5), new Token(Tokens.MINUS, null),
                new Token(Tokens.ID, 6), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.ID, 12), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 5), new Token(Tokens.DIV, null),
                new Token(Tokens.ID, 6), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.ID, 12), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 5), new Token(Tokens.MULT, null),
                new Token(Tokens.ID, 6), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.IF, null), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 5), new Token(Tokens.LT, null),
                new Token(Tokens.ID, 6), new Token(Tokens.ANDTHEN, null), new Token(Tokens.ID, 6), new Token(Tokens.LT, null),
                new Token(Tokens.ID, 5), new Token(Tokens.ORELSE, null), new Token(Tokens.ID, 5), new Token(Tokens.GTEQ, null),
                new Token(Tokens.ID, 6), new Token(Tokens.ORELSE, null), new Token(Tokens.ID, 6), new Token(Tokens.GTEQ, null),
                new Token(Tokens.ID, 5), new Token(Tokens.RPAREN, null), new Token(Tokens.LOOP, null), new Token(Tokens.LCRLY, null),
                new Token(Tokens.ID, 5), new Token(Tokens.ASSIGN, null), new Token(Tokens.ID, 5), new Token(Tokens.MINUS, null),
                new Token(Tokens.NUM, 1), new Token(Tokens.SEMI, null), new Token(Tokens.IF, null), new Token(Tokens.LPAREN, null),
                new Token(Tokens.ID, 5), new Token(Tokens.LT, null), new Token(Tokens.NUM, 0), new Token(Tokens.RPAREN, null),
                new Token(Tokens.EXIT, null), new Token(Tokens.SEMI, null), new Token(Tokens.ELSE, null), new Token(Tokens.CONTINUE, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.RCRLY, null), new Token(Tokens.END, null), new Token(Tokens.RCRLY, null), new Token(Tokens.ID, 13),
                new Token(Tokens.LPAREN, null), new Token(Tokens.NUM, 0), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.RCRLY, null), new Token(Tokens.ENDFILE, null)
        ));
        testCases.put(fileName, new Test(fileName, expectedTokens));
//        new Test("input.cs16", new ArrayList<>()) {
//        };
//        new Test("keywords.cs16", new ArrayList<>()) {
//        };
//        new Test("general_tokens.cs16", new ArrayList<>()) {
//        };
        //new Test("commentHell.cs16", new ArrayList<>()){};
        return testCases;
    }

}//new Token(Tokens.,null)    ,new Token(Tokens.,null),    new Token(Tokens.,null)    ,new Token(Tokens.,null),
