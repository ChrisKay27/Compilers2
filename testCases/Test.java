package testCases;

import admininstration.Options;
import admininstration.TestAdmin;
import parser.Tokens;
import scanner.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private static final String TEST_CASE_PATH = "testCases/";
    //
    protected List<Token> expectedTokens;
    private static List<Token> libraryTokens;

    static {
        libraryTokens = Arrays.asList(
                new Token(Tokens.INT, null), new Token(Tokens.ID, 0), new Token(Tokens.LPAREN, null), new Token(Tokens.VOID, null), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.VOID, null), new Token(Tokens.ID, 1), new Token(Tokens.LPAREN, null), new Token(Tokens.INT, null), new Token(Tokens.ID, 2), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.BOOL, null), new Token(Tokens.ID, 3), new Token(Tokens.LPAREN, null), new Token(Tokens.VOID, null), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.VOID, null), new Token(Tokens.ID, 4), new Token(Tokens.LPAREN, null), new Token(Tokens.BOOL, null), new Token(Tokens.ID, 2), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null)
                );
    }

    private final Options options;
    protected String name;
    private TestAdmin admin;
    private boolean traceEnabled;

    //
    public Test(String name, List<Token> expectedTokens, Options options) {
        this.name = name;
        this.expectedTokens = expectedTokens;
        this.options = options;
        this.traceEnabled = options.verbose;
    }

    public boolean run() {
        options.inputFilePath = TEST_CASE_PATH + this.name;
        try {
            admin = new TestAdmin(options);
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

    public static boolean runAll(Options options) {

        boolean res = true;
        for (Test test : getTestCases(options).values()) {

            if( options.verbose )
                System.out.println("Running test: " + test.name);

            boolean success = test.run();

            if( options.verbose ) {
                if (!success) {
                    System.out.println("Expected Tokens:" + test.expectedTokens);
                    System.out.println("Received Tokens:" + test.admin.getParse());
                }
                //else
                //    System.out.println("Passed");
            }

            res &= success;
        }
        return res;
    }


    public static HashMap<String, Test> getTestCases(Options options) {
        HashMap<String, Test> testCases = new HashMap<>();

        String fileName = "simple.cs16";
        List<Token> expectedTokens = new ArrayList(Test.libraryTokens);
        expectedTokens.addAll(Arrays.asList(new Token(Tokens.IF, null),
                new Token(Tokens.LPAREN, null),
                new Token(Tokens.ID, 2),
                new Token(Tokens.GT, null),
                new Token(Tokens.NUM, 0),
                new Token(Tokens.RPAREN, null),
                new Token(Tokens.ID, 5),
                new Token(Tokens.ASSIGN, null),
                new Token(Tokens.ID, 2),
                new Token(Tokens.PLUS, null),
                new Token(Tokens.NUM, 1),
                new Token(Tokens.SEMI, null),
                new Token(Tokens.ENDFILE, null)));
        testCases.put(fileName, new Test(fileName, expectedTokens, options));

        fileName = "input.cs16";
        expectedTokens = new ArrayList(Test.libraryTokens);
        expectedTokens.addAll(Arrays.asList(
                new Token(Tokens.INT, null), new Token(Tokens.ID, 2), new Token(Tokens.ASSIGN, null), new Token(Tokens.NUM, 0),
                new Token(Tokens.SEMI, null), new Token(Tokens.INT, null), new Token(Tokens.ID, 5), new Token(Tokens.ASSIGN, null),
                new Token(Tokens.NUM, 0), new Token(Tokens.SEMI, null), new Token(Tokens.ID, 6), new Token(Tokens.ID, 7),
                new Token(Tokens.VOID, null), new Token(Tokens.ID, 8), new Token(Tokens.LPAREN, null), new Token(Tokens.INT, null),
                new Token(Tokens.LSQR, null), new Token(Tokens.RSQR, null), new Token(Tokens.ID, 9), new Token(Tokens.RPAREN, null),
                new Token(Tokens.LCRLY, null), new Token(Tokens.ID, 10), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 2),
                new Token(Tokens.PLUS, null), new Token(Tokens.ID, 5), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.ID, 10), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 2), new Token(Tokens.LSQR, null),
                new Token(Tokens.RSQR, null), new Token(Tokens.PLUS, null), new Token(Tokens.ID, 5), new Token(Tokens.RPAREN, null),
                new Token(Tokens.SEMI, null), new Token(Tokens.IF, null), new Token(Tokens.LPAREN, null), new Token(Tokens.BLIT, 1),
                new Token(Tokens.RPAREN, null), new Token(Tokens.LCRLY, null), new Token(Tokens.ID, 10), new Token(Tokens.LPAREN, null),
                new Token(Tokens.ID, 2), new Token(Tokens.MINUS, null), new Token(Tokens.ID, 5), new Token(Tokens.RPAREN, null),
                new Token(Tokens.SEMI, null), new Token(Tokens.ID, 10), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 2),
                new Token(Tokens.DIV, null), new Token(Tokens.ID, 5), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.ID, 10), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 2), new Token(Tokens.MULT, null),
                new Token(Tokens.ID, 5), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null), new Token(Tokens.IF, null),
                new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 2), new Token(Tokens.LT, null), new Token(Tokens.ID, 5),
                new Token(Tokens.ANDTHEN, null), new Token(Tokens.ID, 5), new Token(Tokens.LT, null), new Token(Tokens.ID, 2),
                new Token(Tokens.ORELSE, null), new Token(Tokens.ID, 2), new Token(Tokens.GTEQ, null), new Token(Tokens.ID, 5),
                new Token(Tokens.ORELSE, null), new Token(Tokens.ID, 5), new Token(Tokens.GTEQ, null), new Token(Tokens.ID, 2),
                new Token(Tokens.RPAREN, null), new Token(Tokens.LOOP, null), new Token(Tokens.LCRLY, null), new Token(Tokens.ID, 2),
                new Token(Tokens.ASSIGN, null), new Token(Tokens.ID, 2), new Token(Tokens.MINUS, null), new Token(Tokens.NUM, 1),
                new Token(Tokens.SEMI, null), new Token(Tokens.IF, null), new Token(Tokens.LPAREN, null), new Token(Tokens.ID, 2),
                new Token(Tokens.LT, null), new Token(Tokens.NUM, 0), new Token(Tokens.RPAREN, null), new Token(Tokens.EXIT, null),
                new Token(Tokens.SEMI, null), new Token(Tokens.ELSE, null), new Token(Tokens.CONTINUE, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.RCRLY, null), new Token(Tokens.END, null), new Token(Tokens.RCRLY, null), new Token(Tokens.ID, 11),
                new Token(Tokens.LPAREN, null), new Token(Tokens.NUM, 0), new Token(Tokens.RPAREN, null), new Token(Tokens.SEMI, null),
                new Token(Tokens.RCRLY, null), new Token(Tokens.ENDFILE, null)
        ));
        testCases.put(fileName, new Test(fileName, expectedTokens, options));


        fileName = "keywords.cs16";
        expectedTokens = new ArrayList(Test.libraryTokens);
        expectedTokens.addAll( Arrays.asList(new Token(Tokens.IF,null),
                new Token(Tokens.ELSE,null),
                new Token(Tokens.LOOP,null),
                new Token(Tokens.BLIT,1),
                new Token(Tokens.BLIT,0),
                new Token(Tokens.END,null),
                new Token(Tokens.EXIT,null),
                new Token(Tokens.VOID,null),
                new Token(Tokens.INT,null),
                new Token(Tokens.ID,5),
                new Token(Tokens.AND,null),
                new Token(Tokens.OR,null),
                new Token(Tokens.NOT,null),
                new Token(Tokens.CONTINUE,null),
                new Token(Tokens.ID,6),
                new Token(Tokens.EXIT,null),
                new Token(Tokens.END,null),
                new Token(Tokens.ID,7),
                new Token(Tokens.LOOP,null),
                new Token(Tokens.CASE,null),
                new Token(Tokens.DEFAULT,null),
                new Token(Tokens.BOOL,null),
                new Token(Tokens.ASSIGN,null),
                new Token(Tokens.LPAREN,null),
                new Token(Tokens.RPAREN,null),
                new Token(Tokens.LSQR,null),
                new Token(Tokens.RSQR,null),
                new Token(Tokens.LCRLY,null),
                new Token(Tokens.RCRLY,null),
                new Token(Tokens.MINUS,null),
                new Token(Tokens.PLUS,null),
                new Token(Tokens.DIV,null),
                new Token(Tokens.MULT,null),
                new Token(Tokens.MOD,null),
                new Token(Tokens.SEMI,null),
                new Token(Tokens.ANDTHEN,null),
                new Token(Tokens.ORELSE,null),
                new Token(Tokens.LT,null),
                new Token(Tokens.LTEQ,null),
                new Token(Tokens.GT,null),
                new Token(Tokens.GTEQ,null),
                new Token(Tokens.EQ,null),
                new Token(Tokens.ASSIGN,null),
                new Token(Tokens.EQ,null),
                new Token(Tokens.ERROR,"& at line:46 col:2"),
                new Token(Tokens.ERROR,"& at line:46 col:7"),
                new Token(Tokens.NEQ,null),
                new Token(Tokens.COLON,null),
                new Token(Tokens.COMMA,null),
                new Token(Tokens.VOID,null),
                new Token(Tokens.RETURN,null),
                new Token(Tokens.REF,null),
                new Token(Tokens.ENDFILE, null)
        ));
        testCases.put(fileName, new Test(fileName, expectedTokens, options));


        fileName = "keywordsContinuous.cs16";
        expectedTokens = new ArrayList(Test.libraryTokens);
        expectedTokens.addAll( Arrays.asList(new Token(Tokens.IF,null),
                new Token(Tokens.ELSE,null),
                new Token(Tokens.LOOP,null),
                new Token(Tokens.BLIT,1),
                new Token(Tokens.BLIT,0),
                new Token(Tokens.END,null),
                new Token(Tokens.EXIT,null),
                new Token(Tokens.VOID,null),
                new Token(Tokens.INT,null),
                new Token(Tokens.ID,5),
                new Token(Tokens.AND,null),
                new Token(Tokens.OR,null),
                new Token(Tokens.NOT,null),
                new Token(Tokens.CONTINUE,null),
                new Token(Tokens.ID,6),
                new Token(Tokens.EXIT,null),
                new Token(Tokens.END,null),
                new Token(Tokens.ID,7),
                new Token(Tokens.LOOP,null),
                new Token(Tokens.CASE,null),
                new Token(Tokens.DEFAULT,null),
                new Token(Tokens.BOOL,null),
                new Token(Tokens.ASSIGN,null),
                new Token(Tokens.LPAREN,null),
                new Token(Tokens.RPAREN,null),
                new Token(Tokens.LSQR,null),
                new Token(Tokens.RSQR,null),
                new Token(Tokens.LCRLY,null),
                new Token(Tokens.RCRLY,null),
                new Token(Tokens.MINUS,null),
                new Token(Tokens.PLUS,null),
                new Token(Tokens.DIV,null),
                new Token(Tokens.MULT,null),
                new Token(Tokens.MOD,null),
                new Token(Tokens.SEMI,null),
                new Token(Tokens.ANDTHEN,null),
                new Token(Tokens.ORELSE,null),
                new Token(Tokens.LT,null),
                new Token(Tokens.LTEQ,null),
                new Token(Tokens.GT,null),
                new Token(Tokens.GTEQ,null),
                new Token(Tokens.EQ,null),
                new Token(Tokens.ASSIGN,null),
                new Token(Tokens.EQ,null),
                new Token(Tokens.ERROR,"& at line:23 col:37"),
                new Token(Tokens.ERROR,"& at line:23 col:42"),
                new Token(Tokens.NEQ,null),
                new Token(Tokens.COLON,null),
                new Token(Tokens.COMMA,null),
                new Token(Tokens.VOID,null),
                new Token(Tokens.RETURN,null),
                new Token(Tokens.REF,null),
                new Token(Tokens.ID,8),
                new Token(Tokens.ENDFILE, null)
        ));
        testCases.put(fileName, new Test(fileName, expectedTokens, options));

        fileName = "errors.cs16";
        expectedTokens = new ArrayList(Test.libraryTokens);
        expectedTokens.addAll(
                Arrays.asList(
                        new Token(Tokens.ERROR, "& at line:1 col:2"),
                        new Token(Tokens.ERROR, "| at line:1 col:3"),
                        new Token(Tokens.ERROR, "! at line:1 col:4"),
                        new Token(Tokens.ERROR, "\" at line:1 col:5"),
                        new Token(Tokens.ERROR, "# at line:1 col:6"),
                        new Token(Tokens.ERROR, "$ at line:1 col:7"),
                        new Token(Tokens.ERROR, "% at line:1 col:8"),
                        new Token(Tokens.ERROR, "\\ at line:1 col:9"),
                        new Token(Tokens.ERROR, "' at line:1 col:10"),
                        new Token(Tokens.ERROR, ". at line:1 col:11"),
                        new Token(Tokens.ERROR, "? at line:1 col:12"),
                        new Token(Tokens.ERROR, "@ at line:1 col:13"),
                        new Token(Tokens.ERROR, "^ at line:1 col:14"),
                        new Token(Tokens.ERROR, "_ at line:1 col:15"),
                        new Token(Tokens.ERROR, "` at line:1 col:16"),
                        new Token(Tokens.ERROR, "~ at line:1 col:17"),
                        new Token(Tokens.NUM, 3),
                        new Token(Tokens.ID, 5),
                        new Token(Tokens.ERROR, ". at line:2 col:8"),
                        new Token(Tokens.ERROR, "$ at line:2 col:9"),
                        new Token(Tokens.NUM, 34),
                        new Token(Tokens.ID, 6),
                        new Token(Tokens.ERROR, "` at line:2 col:15"),
                        new Token(Tokens.ID, 7),
                        new Token(Tokens.ERROR, "~ at line:2 col:20"),
                        new Token(Tokens.NUM, 12),
                        new Token(Tokens.ID, 8), new Token(Tokens.ERROR, "% at line:2 col:25"),
                        new Token(Tokens.ERROR, "Number format exception For input string: \"348653445994559459694596\" at line:3 col:25"),
                        new Token(Tokens.MINUS, null),
                        new Token(Tokens.ERROR, "Number format exception For input string: \"344329493299993929310031\" at line:4 col:26"),
                        new Token(Tokens.NUM, 2147483647),
                        new Token(Tokens.ERROR, "Number format exception For input string: \"2147483648\" at line:6 col:11"),
                        new Token(Tokens.ID, 9)
                )
        );
        testCases.put(fileName, new Test(fileName, expectedTokens, options));

        fileName = "commentHell.cs16";
        expectedTokens = new ArrayList(Test.libraryTokens);
        expectedTokens.addAll( Arrays.asList(new Token(Tokens.ENDFILE, null)  ));
        testCases.put(fileName, new Test(fileName, expectedTokens, options));

//        fileName = "hugeFile.cs16";
//        expectedTokens = Arrays.asList( new Token(Tokens.ENDFILE,null)
//        );
//        testCases.put(fileName, new Test(fileName, expectedTokens, options));
//        new Test("general_tokens.cs16", new ArrayList<>()) {
//        };
        //new Test("commentHell.cs16", new ArrayList<>()){};
        return testCases;
    }


    public static void main(String[] args) {
        String srcFilePath = null;
        String errorLogFilePath = null;
        String outputFilePath = null;
        boolean traceEnabled = false;
        boolean quietEnabled = false;
        boolean verboseEnabled = false;

        boolean tuplePhase = false, lexicalPhase = false, semanticPhase = false, parsePhase = false, compilePhase = false;

        try {
            errorLogFilePath = getTrailingFilePath(args, ERROR, ERROR2);
            outputFilePath = getTrailingFilePath(args, OUTPUT, OUTPUT2);
            quietEnabled = hasOption(args, QUIET);
            verboseEnabled = hasOption(args, VERBOSE, VERBOSE2);

            lexicalPhase = hasOption(args, LEXICAL_PHASE, LEXICAL_PHASE2);
            parsePhase = hasOption(args, PARSER_PHASE, PARSER_PHASE2);
            semanticPhase = hasOption(args, SEMANTIC_PHASE, SEMANTIC_PHASE2);
            tuplePhase = hasOption(args, TUPLE_PHASE, TUPLE_PHASE2);
            compilePhase = hasOption(args, COMPILE_PHASE, COMPILE_PHASE2);
        } catch (Exception e) {
            System.err.println("Error parsing parameters, try -t Path/To/File.cs16 -e Path/To/FileLogFile.txt");
            System.exit(2);
        }

        Options options = new Options(quietEnabled, verboseEnabled, tuplePhase, parsePhase, compilePhase, lexicalPhase, semanticPhase, outputFilePath, errorLogFilePath, srcFilePath);


//        TestAdmin admin;
//        try {
//            admin = new TestAdmin(options);
//            admin.compile();
//            admin.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        System.out.println("Tests results: " + Test.runAll(options));
    }

}
