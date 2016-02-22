package testCases;

import admininstration.Options;
import admininstration.TestAdmin;
import util.WTFException;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by chris_000 on 2/21/2016.
 */
public class ParserTest {

    private static final String TEST_CASE_PATH = "src/testCases/parserTestCases/correct/";
    private static final String ERROR_TEST_CASE_PATH = "src/testCases/parserTestCases/error/";
    private static final String ERROR_MESSAGES_TEST_CASE_PATH = "src/testCases/parserTestCases/expectedErrorOutput/";

    private final List<String> expectedErrorMessages;
    private final Options options;
    private final boolean errorFile;
    protected String testFileName;
    private TestAdmin admin;
    private boolean traceEnabled;

    //
    public ParserTest(String testFileName, List<String> expectedErrorMessages, Options options, boolean errorFile) {
        this.testFileName = testFileName;
        this.expectedErrorMessages = expectedErrorMessages;
        this.options = options;
        this.errorFile = errorFile;
        this.traceEnabled = options.verbose;
    }

    public boolean run() {
        options.inputFilePath = (errorFile?ERROR_TEST_CASE_PATH:TEST_CASE_PATH) + testFileName;
        try {
            admin = new TestAdmin(options);
            admin.compile();

            List<String> errorLog = new ArrayList<>();

            //Get the error log entries in the correct format...
            admin.getOutputHandler().getErrorLog().forEach(line -> {
                line = line.replaceAll("( |\\t)","");
                String[] lines = line.split("\n");
                Collections.addAll(errorLog, lines);
            });


//            System.out.println("Error log size:" + errorLog.size());

            boolean passed = errorLog.equals(expectedErrorMessages);
            if( passed )
                System.out.println(testFileName+" passed.\n");
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
        options.unitTesting = true;
        boolean res = true;
        for (ParserTest test : getTestCases(options)) {

            out.println("\n\n-------------------------------------\n");
            out.println("Running "+(test.errorFile?"error ":"")+"test: " + test.testFileName);

            boolean success = test.run();
            res &= success;
        }
        return res;
    }


    public static List<ParserTest> getTestCases(Options options) {
        List<ParserTest> testCases = new ArrayList<>();

        File correctFolder = new File(TEST_CASE_PATH);
        File[] correctFiles = correctFolder.listFiles();

        if( correctFiles != null ){
            for(File f : correctFiles) {
                String testFileName = f.getName();
                List<String> expectedOutput = new ArrayList<>();
                ParserTest ts = new ParserTest(testFileName, expectedOutput, options, false);
                testCases.add(ts);
            }
        }

        File errorFolder = new File(ERROR_TEST_CASE_PATH);
        File[] errorFiles = errorFolder.listFiles();
        File expectedErrorOutputFolder = new File(ERROR_MESSAGES_TEST_CASE_PATH);
        File[] expectedErrorOutputFiles = expectedErrorOutputFolder.listFiles();

        if( errorFiles != null ){
            for(File f : errorFiles) {
                String testFileName = f.getName();
                List<String> expectedOutput = getExpectedOutput(f,expectedErrorOutputFiles);

                ParserTest ts = new ParserTest(testFileName, expectedOutput, options, true);
                testCases.add(ts);
//                System.out.println("Adding error file test: " + f.getName());
            }
        }

        return testCases;
    }

    public static List<String> getExpectedOutput(File f,File[] expectedErrorOutputFiles){
        for(File errorLog : expectedErrorOutputFiles){
            if( f.getName().equals(errorLog.getName())){
//                System.out.println("Found error log file for " + f.getName());
                List<String> error = new ArrayList<>();

                try {
                    BufferedReader r = new BufferedReader(new FileReader(errorLog));
                    r.lines().forEach(line -> {

//                        System.out.println(line);
//                        System.out.println(line.replaceAll("( |\\t)",""));
                        error.add(line.replaceAll("( |\\t)",""));
                    });
                    r.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new WTFException(e);
                }



                return error;
            }
        }
        throw new WTFException("Could not find output file for this test case! (" + f.getName());
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