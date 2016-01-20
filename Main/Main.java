package Main;

import admininstration.Administration;
import admininstration.Options;

import static java.lang.System.out;

/**
 * Compilers
 *
 * @Author Carston Schilds and Chris Kaebe
 * Date -  1/13/2016.
 */
public class Main {

    public static final String HELP_MENU = "-h",HELP_MENU2 = "-help";
    public static final String LEXICAL_PHASE = "-l",LEXICAL_PHASE2 = "-lex";
    public static final String PARSER_PHASE = "-p",PARSER_PHASE2 = "-parse";
    public static final String SEMANTIC_PHASE = "-s",SEMANTIC_PHASE2 = "-sem";
    public static final String TUPLE_PHASE = "-t",TUPLE_PHASE2 = "-tup";
    public static final String COMPILE_PHASE = "-c",COMPILE_PHASE2 = "-compile";
    public static final String QUIET = "-q",QUIET2 = "-quiet";
    public static final String VERBOSE = "-v",VERBOSE2 = "-verbose";
    public static final String OUTPUT = "-o",OUTPUT2 = "-out";
    public static final String ERROR = "-e",ERROR2 = "-err";

    public static void main(String[] args) {

        String srcFilePath = null;
        String errorLogFilePath = null;
        String outputFilePath = null;
        boolean quietEnabled = false;
        boolean verboseEnabled = false;
        boolean help;
        boolean tuplePhase = false, lexicalPhase = false, semanticPhase = false, parsePhase = false, compilePhase = false;

        try {
            help = hasOption(args,HELP_MENU) || hasOption(args,HELP_MENU2);
            if( help ){
                printHelpMenu();
                System.exit(1);
            }
            errorLogFilePath = getTrailingFilePath(args,ERROR,ERROR2);
            srcFilePath = getInputFilePath(args);
            outputFilePath = getTrailingFilePath(args,OUTPUT,OUTPUT2);
            quietEnabled = hasOption(args,QUIET);
            verboseEnabled = hasOption(args,VERBOSE,VERBOSE2);

            lexicalPhase = hasOption(args,LEXICAL_PHASE,LEXICAL_PHASE2);
            parsePhase = hasOption(args,PARSER_PHASE,PARSER_PHASE2);
            semanticPhase = hasOption(args,SEMANTIC_PHASE, SEMANTIC_PHASE2);
            tuplePhase = hasOption(args,TUPLE_PHASE, TUPLE_PHASE2);
            compilePhase = hasOption(args,COMPILE_PHASE, COMPILE_PHASE2);
        }
        catch(Exception e){
            System.err.println("Error parsing parameters, try -t Path/To/File.cs16 -e Path/To/FileLogFile.txt");
            System.exit(2);
        }

        Options options  = new Options(quietEnabled,verboseEnabled,tuplePhase, parsePhase,compilePhase, lexicalPhase,semanticPhase,outputFilePath,errorLogFilePath,srcFilePath);

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

    public static String getTrailingFilePath(String[] args, String flag1, String flag2) {

        for (int i = 0; i < args.length; i++) {
            String s = args[i];

            if (flag1.equals(s) || flag2.equals(s))
                return args[++i];
        }
        return null;
    }


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
               "\t  \n" +
               "\n" +
               "Examples:\n" +
               "\t  java Main -l -v test1.cs16 test.cs16  -- Scan test1.txt and test2.txt verbosely\n" +
               "\t  java Main -o myapp myapp.cs16     -- Compile myapp.txt to executable the compiler and the completed scanner. \n *");
    }

    public static String getInputFilePath(String[] args){
        boolean ignoreNext = false;
        for (int i = 0; i < args.length; i++) {
            if( ignoreNext ){
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

//    public static String getErrorLogFilePath(String[] args){
//        for (int i = 0; i < args.length; i++) {
//            String s = args[i];
//
//            if (ERROR.equals(s) || ERROR2.equals(s))
//                return args[++i];
//        }
//        return null;
//    }

    public static boolean hasOption( String[] args, String... flags){
        for (String s : args)
            for(String flag : flags)
                if (flag.equals(s))
                    return true;
        return false;
    }
}
