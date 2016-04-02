package unitTesting;

import admininstration.Options;
import admininstration.TestAdmin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by chris_000 on 2/21/2016.
 */
public class QuadGenTester {

    private static final String TEST_CASE_PATH = "src/testCases/quadTestCases/";

    private final List<String> expectedErrorMessages;
    private final Options options;

    protected String testFileName;
    private TestAdmin admin;
    private boolean traceEnabled;

    //
    public QuadGenTester(String testFileName, List<String> expectedErrorMessages, Options options) {
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

            List<String> errorLog = new ArrayList<>();
            List<String> shrunkErrorLog = new ArrayList<>();

            //Get the error log entries in the correct format...
            admin.getOutputHandler().getErrorLog().forEach(line -> {
                //Collect the actual lines
                String[] lines = line.split("\n");
                Collections.addAll(errorLog, lines);

                //Collect the lines with whitespace removed for comparison...
                line = line.replaceAll("( |\\t)", "");
                lines = line.split("\n");
                Collections.addAll(shrunkErrorLog, lines);
            });

            List<String> shrunkExpectedErrorMessages = new ArrayList<>();
            expectedErrorMessages.forEach(line -> shrunkExpectedErrorMessages.add(line.replaceAll("( |\\t)", "")));

//            System.out.println("Error log size:" + errorLog.size());

            boolean passed = shrunkErrorLog.equals(shrunkExpectedErrorMessages);
            if (passed)
                System.out.println(testFileName + " passed.\n");
            else {
                System.out.println(testFileName + " failed.\n");

                System.out.println("Was expecting error output:");
                expectedErrorMessages.forEach(System.out::println);
                System.out.println("\nBut received:");
                errorLog.forEach(System.out::println);
            }

            admin.close();
            return passed;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean runAll(Options options) {
        out.println("\nRunning Tuple Generation Tests");
        options.unitTesting = true;
        boolean res = true;
        for (QuadGenTester test : getTestCases(options)) {

            out.println("\n-------------------------------------\n");
            out.println("Running test: " + test.testFileName);

            boolean success = test.run();
            res &= success;
        }
        return res;
    }


    public static List<QuadGenTester> getTestCases(Options options) {
        List<QuadGenTester> testCases = new ArrayList<>();

        File correctFolder = new File(TEST_CASE_PATH);
        File[] correctFiles = correctFolder.listFiles();

        if (correctFiles != null) {
            for (File f : correctFiles) {
                String testFileName = f.getName();
                List<String> expectedOutput = new ArrayList<>();
                QuadGenTester ts = new QuadGenTester(testFileName, expectedOutput, options);
                testCases.add(ts);
            }
        }

        return testCases;
    }
}
/*
if( errorFile ) {
                    File expectedErrorLog = new File(ERROR_MESSAGES_TEST_CASE_PATH + testFileName);
                    if (!expectedErrorLog.exists())
                        expectedErrorLog.createNewFile();
                    Writer bw = new BufferedWriter(new FileWriter(expectedErrorLog));
                    admin.getOutputHandler().getErrorLog().forEach(s -> {
                    System.out.println("Writing to error file:" +expectedErrorLog.getName()+" -> " + s);
                        try {
                            bw.write(s + '\n');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    bw.close();
                }
 */
