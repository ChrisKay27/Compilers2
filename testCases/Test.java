package testCases;

import admininstration.TestAdmin;
import parser.Tokens;
import scanner.Token;

import java.util.*;

import static Main.Main.*;

/**
 * Concept test cases class, perhaps how the test cases are structured needs some work
 * but the idea that there is a class that handles the testing will be useful.
 *
 * @Author Carston
 * Date -  1/12/2016.
 */
public class Test {
    //
    private static final String TEST_CASE_PATH = "src/testCases/";
    //
    protected List<Token> expectedTokens;
    protected String name;
    private TestAdmin admin;
    private boolean traceEnabled;

    //
    public Test(String name, List<Token> expectedTokens, boolean traceEnabled) {
        this.name = name;
        this.expectedTokens = expectedTokens;
        this.traceEnabled = traceEnabled;
    }

    public boolean run() {
        try {
            admin = new TestAdmin(TEST_CASE_PATH + this.name, traceEnabled);
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

    public static boolean runAll(boolean traceEnabled) {

        boolean res = true;
        for (Test test : getTestCases(traceEnabled).values()) {
            //String name = test.getKey();
            boolean success = test.run();
            if(!success){
                System.out.println("Expected Tokens:" + test.expectedTokens);
                System.out.println("Received Tokens:" + test.admin.getParse());
            }

            res &= success;

        }
        return res;
    }


    public static HashMap<String, Test> getTestCases(boolean traceEnabled) {
        HashMap<String, Test> testCases = new HashMap<>();

        String fileName = "simple.cs16";
        List<Token> expectedTokens = Arrays.asList(new Token(Tokens.IF,null),new Token(Tokens.LPAREN,null),new Token(Tokens.ID,0),
                new Token(Tokens.GT,null),new Token(Tokens.NUM,0),new Token(Tokens.RPAREN,null),new Token(Tokens.ID,1),new Token(Tokens.ASSIGN,null),
                new Token(Tokens.ID,0),new Token(Tokens.PLUS,null),new Token(Tokens.NUM,1),new Token(Tokens.SEMI,null),new Token(Tokens.ENDFILE,null));
        testCases.put(fileName, new Test(fileName, expectedTokens, traceEnabled));

        fileName = "input.cs16";
        expectedTokens = Arrays.asList(new Token(Tokens.INT,null),new Token(Tokens.ID,0),new Token(Tokens.ASSIGN,null),
                new Token(Tokens.NUM,0),new Token(Tokens.SEMI,null),new Token(Tokens.INT,null),new Token(Tokens.ID,1),new Token(Tokens.ASSIGN,null),
                new Token(Tokens.NUM,0),new Token(Tokens.SEMI,null), new Token(Tokens.ID,2),new Token(Tokens.ID,3),new Token(Tokens.VOID,null),new Token(Tokens.ID,4),
                new Token(Tokens.LPAREN,null),new Token(Tokens.ID,5),new Token(Tokens.LSQR,null),new Token(Tokens.RSQR,null),new Token(Tokens.ID,6),
                new Token(Tokens.RPAREN,null), new Token(Tokens.LCRLY,null),new Token(Tokens.ID,7),new Token(Tokens.LPAREN,null),new Token(Tokens.ID,0),
                new Token(Tokens.PLUS,null),new Token(Tokens.ID,1),new Token(Tokens.RPAREN,null),new Token(Tokens.SEMI,null),new Token(Tokens.ID,7),
                new Token(Tokens.LPAREN,null), new Token(Tokens.ID,0),new Token(Tokens.LSQR,null),new Token(Tokens.RSQR,null),new Token(Tokens.PLUS,null),
                new Token(Tokens.ID,1) ,new Token(Tokens.RPAREN,null), new Token(Tokens.SEMI,null) ,new Token(Tokens.IF,null),
                new Token(Tokens.LPAREN,null) ,new Token(Tokens.BLIT,1), new Token(Tokens.RPAREN,null) ,new Token(Tokens.LCRLY,null),
                new Token(Tokens.ID,7) ,new Token(Tokens.LPAREN,null), new Token(Tokens.ID,0)  ,new Token(Tokens.MINUS,null),
                new Token(Tokens.ID,1) ,new Token(Tokens.RPAREN,null), new Token(Tokens.SEMI,null) ,
                new Token(Tokens.ID,7) ,new Token(Tokens.LPAREN,null), new Token(Tokens.ID,0) ,new Token(Tokens.DIV,null),
                new Token(Tokens.ID,1) ,new Token(Tokens.RPAREN,null), new Token(Tokens.SEMI,null)   ,
                new Token(Tokens.ID,7) ,new Token(Tokens.LPAREN,null), new Token(Tokens.ID,0) ,new Token(Tokens.MULT,null),
                new Token(Tokens.ID,1) ,new Token(Tokens.RPAREN,null), new Token(Tokens.SEMI,null),
                new Token(Tokens.IF,null) ,new Token(Tokens.LPAREN,null), new Token(Tokens.ID,0),new Token(Tokens. LT,null),
                new Token(Tokens.ID,1) ,new Token(Tokens.ANDTHEN,null), new Token(Tokens.ID,1) ,new Token(Tokens.LT,null),
                new Token(Tokens.ID,0) ,new Token(Tokens.ORELSE,null), new Token(Tokens.ID,0) ,new Token(Tokens.GTEQ,null),
                new Token(Tokens.ID,1) ,new Token(Tokens.ORELSE,null), new Token(Tokens.ID,1) ,new Token(Tokens.GTEQ,null),
                new Token(Tokens.ID,0) ,new Token(Tokens.RPAREN,null), new Token(Tokens.LOOP,null) ,new Token(Tokens.LCRLY,null),
                new Token(Tokens.ID,0) ,new Token(Tokens.ASSIGN,null), new Token(Tokens.ID,0) ,new Token(Tokens.MINUS,null),
                new Token(Tokens.NUM,1) ,new Token(Tokens.SEMI,null), new Token(Tokens.IF,null) ,new Token(Tokens.LPAREN,null),
                new Token(Tokens.ID,0) ,new Token(Tokens.LT,null), new Token(Tokens.NUM,0) ,new Token(Tokens.RPAREN,null),
                new Token(Tokens.EXIT,null) ,new Token(Tokens.SEMI,null), new Token(Tokens.ELSE,null) ,new Token(Tokens.CONTINUE,null),
                new Token(Tokens.SEMI,null), new Token(Tokens.RCRLY,null) ,new Token(Tokens.END,null), new Token(Tokens.RCRLY,null) ,
                new Token(Tokens.ID,8), new Token(Tokens.LPAREN,null),new Token(Tokens.NUM,0),new Token(Tokens.RPAREN,null),
                new Token(Tokens.SEMI,null), new Token(Tokens.RCRLY,null), new Token(Tokens.ENDFILE,null)
                );
        testCases.put(fileName, new Test(fileName, expectedTokens, traceEnabled));


        fileName = "keywords.cs16";
        expectedTokens = Arrays.asList(new Token(Tokens.IF,null),new Token(Tokens.ELSE,null),new Token(Tokens.LOOP,null),
                new Token(Tokens.BLIT,1),new Token(Tokens.BLIT,0),new Token(Tokens.END,null),new Token(Tokens.EXIT,null),
                new Token(Tokens.VOID,null),new Token(Tokens.INT,null),new Token(Tokens.ID,0), new Token(Tokens.ENDFILE,null)
        );
        testCases.put(fileName, new Test(fileName, expectedTokens, traceEnabled));


//        new Test("general_tokens.cs16", new ArrayList<>()) {
//        };
        //new Test("commentHell.cs16", new ArrayList<>()){};
        return testCases;
    }


    public static void main(String[] args) {
        String srcFilePath = null;
        String errorLogFilePath = null;
        boolean traceEnabled = false;
        try {
            int x = 0;
            errorLogFilePath = getErrorLogFilePath(args);
            srcFilePath = getInputFilePath(args);
            traceEnabled = isTraceEnabled(args);
        }
        catch(Exception e){
            System.err.println("Error parsing parameters, try -c Path/To/File.cs16 -err Path/To/FileLogFile.txt");
        }

        System.out.println("Tests results: " + Test.runAll(traceEnabled));
    }

}
