package testCases;

import admininstration.Options;
import admininstration.TestAdmin;
import parser.TokenType;
import scanner.Token;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chris_000 on 2/21/2016.
 */
public class ParserTest {

    private static final String TEST_CASE_PATH = "src/testCases/parserTestCases/failTestCases/";

    private final List<String> expectedErrorMessages;
    private final Options options;
    protected String testFileName;
    private TestAdmin admin;
    private boolean traceEnabled;

    //
    public ParserTest(String testFileName, List<String> expectedErrorMessages, Options options) {
        this.testFileName = testFileName;
        this.expectedErrorMessages = expectedErrorMessages;
        this.options = options;
        this.traceEnabled = options.verbose;
    }

    public boolean run() {
        options.inputFilePath = TEST_CASE_PATH + testFileName;
        try {
            admin = new TestAdmin(options);
            admin.compile();

            List<String> errorLog = admin.getOutputHandler().getErrorLog();

            boolean passed = errorLog.equals(expectedErrorMessages);
            if( passed )
                System.out.println(testFileName+" passed.");
            else
                System.out.println(testFileName+" failed.");

            admin.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean runAll(Options options) {

        boolean res = true;
        for (ParserTest test : getTestCases(options).values()) {

            if( options.verbose )
                System.out.println("Running test: " + test.testFileName);

            boolean success = test.run();
            res &= success;
        }
        return res;
    }


    public static HashMap<String, ParserTest> getTestCases(Options options) {
        HashMap<String, ParserTest> testCases = new HashMap<>();

        String testFileName = "missingBrace.cs16";
        List<String> expectedOutput = Arrays.asList("");
        ParserTest ts = new ParserTest(testFileName,expectedOutput,options);

        testCases.put(testFileName,ts);

        return testCases;
    }
}
