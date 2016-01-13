package testCases;

import admininstration.TestAdmin;
import scanner.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Concept test cases class, perhaps how the test cases are structured needs some work
 * but the idea that there is a class that handles the testing will be useful.
 *
 * @Author Carston
 * Date -  1/12/2016.
 */
public abstract class Test {
    //
    private static final String TEST_CASE_PATH = "testCases/";
    private static HashMap<String, Test> testCases = new HashMap<>();
    //
    protected ArrayList<Token> expectedTokens;
    protected String name;

    //
    public Test(String name, ArrayList<Token> expectedTokens) {
        this.name = name;
        this.expectedTokens = expectedTokens;
        testCases.put(name, this);
    }

    public boolean run() {
        TestAdmin admin;
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
        } finally {

        }
        return false;
    }

    public static boolean runAll() {
        boolean res = true;
        for (Map.Entry<String, Test> test : testCases.entrySet()) {
            String name = test.getKey();
            res &= test.getValue().run();
        }
        return res;
    }

    public static HashMap<String, Test> init() {
        new Test("simple.cs16", new ArrayList<>()) {
        };
        new Test("input.cs16", new ArrayList<>()) {
        };
        new Test("keywords.cs16", new ArrayList<>()) {
        };
        new Test("general_tokens.cs16", new ArrayList<>()) {
        };
        //new Test("commentHell.cs16", new ArrayList<>()){};
        return testCases;
    }

}
