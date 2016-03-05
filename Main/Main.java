package Main;

import admininstration.Administration;
import admininstration.Options;
import unitTesting.Test;

import static java.lang.System.out;

/**
 * Compilers
 *
 * @Author Carston Schilds and Chris Kaebe
 * Date -  1/13/2016.
 */
public class Main {
    // help
    public static final String HELP_MENU = "-h", HELP_MENU2 = "-help";
    public static final String LEXICAL_PHASE = "-l", LEXICAL_PHASE2 = "-lex";
    public static final String PARSER_PHASE = "-p", PARSER_PHASE2 = "-parse";
    public static final String SEMANTIC_PHASE = "-s", SEMANTIC_PHASE2 = "-sem";
    public static final String TUPLE_PHASE = "-t", TUPLE_PHASE2 = "-tup";
    public static final String COMPILE_PHASE = "-c", COMPILE_PHASE2 = "-compile";
    public static final String QUIET = "-q", QUIET2 = "-quiet";
    public static final String VERBOSE = "-v", VERBOSE2 = "-verbose";
    public static final String OUTPUT = "-o", OUTPUT2 = "-out";
    public static final String ERROR = "-e", ERROR2 = "-err";
    public static final String UNITTEST = "-unittest";
    public static final String PRINT_AST = "-ast";

    public static void main(String[] args) {
//
//        System.out.println("DEBUG FIRST AND FOLLOW SETS");
//        FirstAndFollowSets.FIRSTofCase_stmt.forEach(System.out::println);
//        System.out.println("END DEBUG");

        if (hasOption(args, UNITTEST)) {
            Test.main(args);
            return;
        }

        String srcFilePath;
        String errorLogFilePath;
        String outputFilePath;
        boolean quietEnabled;
        boolean verboseEnabled;
        boolean help;
        boolean tuplePhase, lexicalPhase, semanticPhase = true, parsePhase, compilePhase;
        boolean printAST;

        try {
            help = hasOption(args, HELP_MENU) || hasOption(args, HELP_MENU2);
            if (help) {
                printHelpMenu();
                System.exit(1);
            }
            errorLogFilePath = getTrailingFilePath(args, ERROR, ERROR2);
            srcFilePath = getInputFilePath(args);
            outputFilePath = getTrailingFilePath(args, OUTPUT, OUTPUT2);
            quietEnabled = hasOption(args, QUIET, QUIET2);
            verboseEnabled = hasOption(args, VERBOSE, VERBOSE2);

            lexicalPhase = hasOption(args, LEXICAL_PHASE, LEXICAL_PHASE2);
            parsePhase = hasOption(args, PARSER_PHASE, PARSER_PHASE2);
            semanticPhase = hasOption(args, SEMANTIC_PHASE, SEMANTIC_PHASE2);
            tuplePhase = hasOption(args, TUPLE_PHASE, TUPLE_PHASE2);
            compilePhase = hasOption(args, COMPILE_PHASE, COMPILE_PHASE2);
            printAST = hasOption(args, PRINT_AST);
        } catch (Exception e) {
            System.err.println("Error parsing parameters, try -t Path/To/File.cs16 -e Path/To/FileLogFile.txt");
            System.exit(2);
            return;
        }

        if( !lexicalPhase && !parsePhase && !tuplePhase && !compilePhase )
            semanticPhase = true;

        Options options = new Options(quietEnabled, verboseEnabled, tuplePhase, parsePhase, compilePhase, lexicalPhase,
                semanticPhase, outputFilePath, errorLogFilePath, srcFilePath, printAST);

        Administration admin;
        try {
            admin = new Administration(options);
            admin.compile();
            admin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //}
    }

    /**
     * Gets the file path for either the error log file or compiler output file and returns it.
     * UNIV | ERROR2 => error log file path
     * OUTPUT | OUTPUT2 => compiler output file path
     *
     * @param args  - String[] args from the command line arguments
     * @param flag1 - Either UNIV, or OUTPUT
     * @param flag2 - Either ERROR2, or OUTPUT2
     * @return String - the file path to either the error log file or compiler output file
     */
    public static String getTrailingFilePath(String[] args, String flag1, String flag2) {

        for (int i = 0; i < args.length; i++) {
            String s = args[i];

            if (flag1.equals(s) || flag2.equals(s))
                return args[++i];
        }

        return null;
    }

    /**
     * Outputs all help text to STDOUT
     */
    public static void printHelpMenu() {
        out.println("****** Help Menu ********");
        out.println("* Available Options *");
        out.println("* -h | -help    \t-- Displays this help menu\n" +
                "-l | -lex\t-- Process up to the Lexer phase\n" +
                "-p | -parse    \t-- Process up to the Parser phase\n" +
                "-s | -sem\t-- Process up to the Semantic Analysis phase\n" +
                "-t | -tup      \t -- Process up to the Tuple phase\n" +
                "-c | -compile  \t-- Process all phases and compile (default behavior)\n" +
                "-q | -quiet    \t-- Only display error messages (default behavior)\n" +
                "-v | -verbose  \t-- Display all trace messages\n" +
                "-o | -out      \t-- Output file (defaults to -out=STDOUT)\n" +
                "-e | -err      \t-- Error file (defaults to -err=STDOUT)\n" +
                "-unittest      \t-- Run our unit tests\n" +
                "\t  \n" +
                "\n" +
                "Examples:\n" +
                "\t  java Main -l -v test1.cs16 test.cs16  -- Scan test1.txt and test2.txt verbosely\n" +
                "\t  java Main -o myapp myapp.cs16     -- Compile myapp.txt to executable the compiler and the completed scanner. \n *");
    }

    /**
     * Takes the command line arguments and scans them for the path argument for the source file, then returns it.
     *
     * @param args - String[] from the command line arguments to main
     * @return - returns the path to the source file to be compiled
     * @throws - RunTimeException when the path couldn't be found in args
     */
    public static String getInputFilePath(String[] args) {
        boolean ignoreNext = false;
        for (int i = 0; i < args.length; i++) {
            if (ignoreNext) {
                ignoreNext = false;
                continue;
            }
            String s = args[i];

            if (s.startsWith(ERROR) || s.startsWith(ERROR2) ||
                    s.startsWith(OUTPUT) || s.startsWith(OUTPUT2))
                ignoreNext = true;

            if (!s.startsWith("-"))
                return s;

        }
        throw new RuntimeException("Did not find input file path!");
    }


    /**
     * Checks the command line arguments for flags that match the list of arguments
     * Returns true when any flag is matched in the command line arguments
     *
     * @param args  - String[] command line arguments
     * @param flags - String... any number of flags to match
     * @return true if there is a match in args from flags, false if nothing matched
     */
    public static boolean hasOption(String[] args, String... flags) {
        for (String s : args)
            for (String flag : flags)
                if (flag.equals(s))
                    return true;
        return false;
    }
}
