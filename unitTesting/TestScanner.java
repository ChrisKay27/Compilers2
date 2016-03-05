package unitTesting;

import admininstration.Options;
import admininstration.TestAdmin;
import parser.TokenType;
import scanner.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Concept test cases class, perhaps how the test cases are structured needs some work
 * but the idea that there is a class that handles the testing will be useful.
 *
 * @Author Carston
 * Date -  1/12/2016.
 */
public class TestScanner {
    //
    private static final String TEST_CASE_PATH = "src/testCases/scannerTestCases";
    //
    protected List<Token> expectedTokens;
    private static List<Token> libraryTokens;

    static {
        libraryTokens = Arrays.asList(
                new Token(TokenType.INT, null), new Token(TokenType.ID, 0), new Token(TokenType.LPAREN, null), new Token(TokenType.VOID, null), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null),
                new Token(TokenType.VOID, null), new Token(TokenType.ID, 1), new Token(TokenType.LPAREN, null), new Token(TokenType.INT, null), new Token(TokenType.ID, 2), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null),
                new Token(TokenType.BOOL, null), new Token(TokenType.ID, 3), new Token(TokenType.LPAREN, null), new Token(TokenType.VOID, null), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null),
                new Token(TokenType.VOID, null), new Token(TokenType.ID, 4), new Token(TokenType.LPAREN, null), new Token(TokenType.BOOL, null), new Token(TokenType.ID, 2), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null)
                );
    }

    private final Options options;
    protected String name;
    private TestAdmin admin;
    private boolean traceEnabled;

    //
    public TestScanner(String name, List<Token> expectedTokens, Options options) {
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
                    "TestScanner " + name + " has failed./n"
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
        for (TestScanner test : getTestCases(options).values()) {

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


    public static HashMap<String, TestScanner> getTestCases(Options options) {
        HashMap<String, TestScanner> testCases = new HashMap<>();

        String fileName = "simple.cs16";
        List<Token> expectedTokens = new ArrayList(TestScanner.libraryTokens);
        expectedTokens.addAll(Arrays.asList(new Token(TokenType.IF, null),
                new Token(TokenType.LPAREN, null),
                new Token(TokenType.ID, 2),
                new Token(TokenType.GT, null),
                new Token(TokenType.NUM, 0),
                new Token(TokenType.RPAREN, null),
                new Token(TokenType.ID, 5),
                new Token(TokenType.ASSIGN, null),
                new Token(TokenType.ID, 2),
                new Token(TokenType.PLUS, null),
                new Token(TokenType.NUM, 1),
                new Token(TokenType.SEMI, null),
                new Token(TokenType.ENDFILE, null)));
        testCases.put(fileName, new TestScanner(fileName, expectedTokens, options));

        fileName = "input.cs16";
        expectedTokens = new ArrayList(TestScanner.libraryTokens);
        expectedTokens.addAll(Arrays.asList(
                new Token(TokenType.INT, null), new Token(TokenType.ID, 2), new Token(TokenType.ASSIGN, null), new Token(TokenType.NUM, 0),
                new Token(TokenType.SEMI, null), new Token(TokenType.INT, null), new Token(TokenType.ID, 5), new Token(TokenType.ASSIGN, null),
                new Token(TokenType.NUM, 0), new Token(TokenType.SEMI, null), new Token(TokenType.ID, 6), new Token(TokenType.ID, 7),
                new Token(TokenType.VOID, null), new Token(TokenType.ID, 8), new Token(TokenType.LPAREN, null), new Token(TokenType.INT, null),
                new Token(TokenType.LSQR, null), new Token(TokenType.RSQR, null), new Token(TokenType.ID, 9), new Token(TokenType.RPAREN, null),
                new Token(TokenType.LCRLY, null), new Token(TokenType.ID, 10), new Token(TokenType.LPAREN, null), new Token(TokenType.ID, 2),
                new Token(TokenType.PLUS, null), new Token(TokenType.ID, 5), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null),
                new Token(TokenType.ID, 10), new Token(TokenType.LPAREN, null), new Token(TokenType.ID, 2), new Token(TokenType.LSQR, null),
                new Token(TokenType.RSQR, null), new Token(TokenType.PLUS, null), new Token(TokenType.ID, 5), new Token(TokenType.RPAREN, null),
                new Token(TokenType.SEMI, null), new Token(TokenType.IF, null), new Token(TokenType.LPAREN, null), new Token(TokenType.BLIT, 1),
                new Token(TokenType.RPAREN, null), new Token(TokenType.LCRLY, null), new Token(TokenType.ID, 10), new Token(TokenType.LPAREN, null),
                new Token(TokenType.ID, 2), new Token(TokenType.MINUS, null), new Token(TokenType.ID, 5), new Token(TokenType.RPAREN, null),
                new Token(TokenType.SEMI, null), new Token(TokenType.ID, 10), new Token(TokenType.LPAREN, null), new Token(TokenType.ID, 2),
                new Token(TokenType.DIV, null), new Token(TokenType.ID, 5), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null),
                new Token(TokenType.ID, 10), new Token(TokenType.LPAREN, null), new Token(TokenType.ID, 2), new Token(TokenType.MULT, null),
                new Token(TokenType.ID, 5), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null), new Token(TokenType.IF, null),
                new Token(TokenType.LPAREN, null), new Token(TokenType.ID, 2), new Token(TokenType.LT, null), new Token(TokenType.ID, 5),
                new Token(TokenType.ANDTHEN, null), new Token(TokenType.ID, 5), new Token(TokenType.LT, null), new Token(TokenType.ID, 2),
                new Token(TokenType.ORELSE, null), new Token(TokenType.ID, 2), new Token(TokenType.GTEQ, null), new Token(TokenType.ID, 5),
                new Token(TokenType.ORELSE, null), new Token(TokenType.ID, 5), new Token(TokenType.GTEQ, null), new Token(TokenType.ID, 2),
                new Token(TokenType.RPAREN, null), new Token(TokenType.LOOP, null), new Token(TokenType.LCRLY, null), new Token(TokenType.ID, 2),
                new Token(TokenType.ASSIGN, null), new Token(TokenType.ID, 2), new Token(TokenType.MINUS, null), new Token(TokenType.NUM, 1),
                new Token(TokenType.SEMI, null), new Token(TokenType.IF, null), new Token(TokenType.LPAREN, null), new Token(TokenType.ID, 2),
                new Token(TokenType.LT, null), new Token(TokenType.NUM, 0), new Token(TokenType.RPAREN, null), new Token(TokenType.EXIT, null),
                new Token(TokenType.SEMI, null), new Token(TokenType.ELSE, null), new Token(TokenType.CONTINUE, null), new Token(TokenType.SEMI, null),
                new Token(TokenType.RCRLY, null), new Token(TokenType.END, null), new Token(TokenType.RCRLY, null), new Token(TokenType.ID, 11),
                new Token(TokenType.LPAREN, null), new Token(TokenType.NUM, 0), new Token(TokenType.RPAREN, null), new Token(TokenType.SEMI, null),
                new Token(TokenType.RCRLY, null), new Token(TokenType.ENDFILE, null)
        ));
        testCases.put(fileName, new TestScanner(fileName, expectedTokens, options));


        fileName = "keywords.cs16";
        expectedTokens = new ArrayList(TestScanner.libraryTokens);
        expectedTokens.addAll( Arrays.asList(new Token(TokenType.IF,null),
                new Token(TokenType.ELSE,null),
                new Token(TokenType.LOOP,null),
                new Token(TokenType.BLIT,1),
                new Token(TokenType.BLIT,0),
                new Token(TokenType.END,null),
                new Token(TokenType.EXIT,null),
                new Token(TokenType.VOID,null),
                new Token(TokenType.INT,null),
                new Token(TokenType.ID,5),
                new Token(TokenType.AND,null),
                new Token(TokenType.OR,null),
                new Token(TokenType.NOT,null),
                new Token(TokenType.CONTINUE,null),
                new Token(TokenType.ID,6),
                new Token(TokenType.EXIT,null),
                new Token(TokenType.END,null),
                new Token(TokenType.ID,7),
                new Token(TokenType.LOOP,null),
                new Token(TokenType.CASE,null),
                new Token(TokenType.DEFAULT,null),
                new Token(TokenType.BOOL,null),
                new Token(TokenType.ASSIGN,null),
                new Token(TokenType.LPAREN,null),
                new Token(TokenType.RPAREN,null),
                new Token(TokenType.LSQR,null),
                new Token(TokenType.RSQR,null),
                new Token(TokenType.LCRLY,null),
                new Token(TokenType.RCRLY,null),
                new Token(TokenType.MINUS,null),
                new Token(TokenType.PLUS,null),
                new Token(TokenType.DIV,null),
                new Token(TokenType.MULT,null),
                new Token(TokenType.MOD,null),
                new Token(TokenType.SEMI,null),
                new Token(TokenType.ANDTHEN,null),
                new Token(TokenType.ORELSE,null),
                new Token(TokenType.LT,null),
                new Token(TokenType.LTEQ,null),
                new Token(TokenType.GT,null),
                new Token(TokenType.GTEQ,null),
                new Token(TokenType.EQ,null),
                new Token(TokenType.ASSIGN,null),
                new Token(TokenType.EQ,null),
                new Token(TokenType.ERROR,"& at line:46 col:2"),
                new Token(TokenType.ERROR,"& at line:46 col:7"),
                new Token(TokenType.NEQ,null),
                new Token(TokenType.COLON,null),
                new Token(TokenType.COMMA,null),
                new Token(TokenType.VOID,null),
                new Token(TokenType.RETURN,null),
                new Token(TokenType.REF,null),
                new Token(TokenType.ENDFILE, null)
        ));
        testCases.put(fileName, new TestScanner(fileName, expectedTokens, options));


        fileName = "keywordsContinuous.cs16";
        expectedTokens = new ArrayList(TestScanner.libraryTokens);
        expectedTokens.addAll( Arrays.asList(new Token(TokenType.IF,null),
                new Token(TokenType.ELSE,null),
                new Token(TokenType.LOOP,null),
                new Token(TokenType.BLIT,1),
                new Token(TokenType.BLIT,0),
                new Token(TokenType.END,null),
                new Token(TokenType.EXIT,null),
                new Token(TokenType.VOID,null),
                new Token(TokenType.INT,null),
                new Token(TokenType.ID,5),
                new Token(TokenType.AND,null),
                new Token(TokenType.OR,null),
                new Token(TokenType.NOT,null),
                new Token(TokenType.CONTINUE,null),
                new Token(TokenType.ID,6),
                new Token(TokenType.EXIT,null),
                new Token(TokenType.END,null),
                new Token(TokenType.ID,7),
                new Token(TokenType.LOOP,null),
                new Token(TokenType.CASE,null),
                new Token(TokenType.DEFAULT,null),
                new Token(TokenType.BOOL,null),
                new Token(TokenType.ASSIGN,null),
                new Token(TokenType.LPAREN,null),
                new Token(TokenType.RPAREN,null),
                new Token(TokenType.LSQR,null),
                new Token(TokenType.RSQR,null),
                new Token(TokenType.LCRLY,null),
                new Token(TokenType.RCRLY,null),
                new Token(TokenType.MINUS,null),
                new Token(TokenType.PLUS,null),
                new Token(TokenType.DIV,null),
                new Token(TokenType.MULT,null),
                new Token(TokenType.MOD,null),
                new Token(TokenType.SEMI,null),
                new Token(TokenType.ANDTHEN,null),
                new Token(TokenType.ORELSE,null),
                new Token(TokenType.LT,null),
                new Token(TokenType.LTEQ,null),
                new Token(TokenType.GT,null),
                new Token(TokenType.GTEQ,null),
                new Token(TokenType.EQ,null),
                new Token(TokenType.ASSIGN,null),
                new Token(TokenType.EQ,null),
                new Token(TokenType.ERROR,"& at line:23 col:37"),
                new Token(TokenType.ERROR,"& at line:23 col:42"),
                new Token(TokenType.NEQ,null),
                new Token(TokenType.COLON,null),
                new Token(TokenType.COMMA,null),
                new Token(TokenType.VOID,null),
                new Token(TokenType.RETURN,null),
                new Token(TokenType.REF,null),
                new Token(TokenType.ID,8),
                new Token(TokenType.ENDFILE, null)
        ));
        testCases.put(fileName, new TestScanner(fileName, expectedTokens, options));

        fileName = "errors.cs16";
        expectedTokens = new ArrayList(TestScanner.libraryTokens);
        expectedTokens.addAll(
                Arrays.asList(
                        new Token(TokenType.ERROR, "& at line:1 col:2"),
                        new Token(TokenType.ERROR, "| at line:1 col:3"),
                        new Token(TokenType.ERROR, "! at line:1 col:4"),
                        new Token(TokenType.ERROR, "\" at line:1 col:5"),
                        new Token(TokenType.ERROR, "# at line:1 col:6"),
                        new Token(TokenType.ERROR, "$ at line:1 col:7"),
                        new Token(TokenType.ERROR, "% at line:1 col:8"),
                        new Token(TokenType.ERROR, "\\ at line:1 col:9"),
                        new Token(TokenType.ERROR, "' at line:1 col:10"),
                        new Token(TokenType.ERROR, ". at line:1 col:11"),
                        new Token(TokenType.ERROR, "? at line:1 col:12"),
                        new Token(TokenType.ERROR, "@ at line:1 col:13"),
                        new Token(TokenType.ERROR, "^ at line:1 col:14"),
                        new Token(TokenType.ERROR, "_ at line:1 col:15"),
                        new Token(TokenType.ERROR, "` at line:1 col:16"),
                        new Token(TokenType.ERROR, "~ at line:1 col:17"),
                        new Token(TokenType.NUM, 3),
                        new Token(TokenType.ID, 5),
                        new Token(TokenType.ERROR, ". at line:2 col:8"),
                        new Token(TokenType.ERROR, "$ at line:2 col:9"),
                        new Token(TokenType.NUM, 34),
                        new Token(TokenType.ID, 6),
                        new Token(TokenType.ERROR, "` at line:2 col:15"),
                        new Token(TokenType.ID, 7),
                        new Token(TokenType.ERROR, "~ at line:2 col:20"),
                        new Token(TokenType.NUM, 12),
                        new Token(TokenType.ID, 8), new Token(TokenType.ERROR, "% at line:2 col:25"),
                        new Token(TokenType.ERROR, "Number format exception For input string: \"348653445994559459694596\" at line:3 col:25"),
                        new Token(TokenType.MINUS, null),
                        new Token(TokenType.ERROR, "Number format exception For input string: \"344329493299993929310031\" at line:4 col:26"),
                        new Token(TokenType.NUM, 2147483647),
                        new Token(TokenType.ERROR, "Number format exception For input string: \"2147483648\" at line:6 col:11"),
                        new Token(TokenType.ID, 9)
                )
        );
        testCases.put(fileName, new TestScanner(fileName, expectedTokens, options));

        fileName = "commentHell.cs16";
        expectedTokens = new ArrayList<>(TestScanner.libraryTokens);
        expectedTokens.addAll( Arrays.asList(new Token(TokenType.ENDFILE, null)  ));
        testCases.put(fileName, new TestScanner(fileName, expectedTokens, options));

//        fileName = "hugeFile.cs16";
//        expectedTokens = Arrays.asList( new Token(TokenType.ENDFILE,null)
//        );
//        testCases.put(fileName, new TestScanner(fileName, expectedTokens, options));
//        new TestScanner("general_tokens.cs16", new ArrayList<>()) {
//        };
        //new TestScanner("commentHell.cs16", new ArrayList<>()){};
        return testCases;
    }


//    public static void main(String[] args) {
//        String srcFilePath = null;
//        String errorLogFilePath = null;
//        String outputFilePath = null;
//        boolean traceEnabled = false;
//        boolean quietEnabled = false;
//        boolean verboseEnabled = false;
//
//        boolean tuplePhase = false, lexicalPhase = false, semanticPhase = false, parsePhase = false, compilePhase = false;
//        boolean printAST;
//
//        try {
//            errorLogFilePath = getTrailingFilePath(args, UNIV, ERROR2);
//            outputFilePath = getTrailingFilePath(args, OUTPUT, OUTPUT2);
//            quietEnabled = hasOption(args, QUIET);
//            verboseEnabled = hasOption(args, VERBOSE, VERBOSE2);
//
//            lexicalPhase = hasOption(args, LEXICAL_PHASE, LEXICAL_PHASE2);
//            parsePhase = hasOption(args, PARSER_PHASE, PARSER_PHASE2);
//            semanticPhase = hasOption(args, SEMANTIC_PHASE, SEMANTIC_PHASE2);
//            tuplePhase = hasOption(args, TUPLE_PHASE, TUPLE_PHASE2);
//            compilePhase = hasOption(args, COMPILE_PHASE, COMPILE_PHASE2);
//            printAST = hasOption(args, PRINT_AST);
//        } catch (Exception e) {
//            System.err.println("Error parsing parameters, try -t Path/To/File.cs16 -e Path/To/FileLogFile.txt");
//            System.exit(2);
//            return;
//        }
//
//        Options options = new Options(quietEnabled, verboseEnabled, tuplePhase, parsePhase, compilePhase, lexicalPhase,
//                semanticPhase, outputFilePath, errorLogFilePath, srcFilePath, printAST);
//
//
////        TestAdmin admin;
////        try {
////            admin = new TestAdmin(options);
////            admin.compile();
////            admin.close();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//        System.out.println("Tests results: " + TestScanner.runAll(options));
//    }

}
