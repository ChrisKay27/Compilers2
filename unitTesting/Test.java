package unitTesting;

import admininstration.Options;

import static Main.Main.*;
import static Main.Main.PRINT_AST;
import static Main.Main.hasOption;

/**
 * Created by chris_000 on 2/21/2016.
 */
public class Test {

    public static void main(String[] args) {
        String srcFilePath = null;
        String errorLogFilePath = null;
        String outputFilePath = null;
        boolean traceEnabled = false;
        boolean quietEnabled = false;
        boolean verboseEnabled = false;

        boolean tuplePhase = false, lexicalPhase = false, semanticPhase = false, parsePhase = false, compilePhase = false;
        boolean printAST;

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
            printAST = hasOption(args, PRINT_AST);
        } catch (Exception e) {
            System.err.println("Error parsing parameters, try -t Path/To/File.cs16 -e Path/To/FileLogFile.txt");
            System.exit(2);
            return;
        }

        Options options = new Options(quietEnabled, verboseEnabled, tuplePhase, parsePhase, compilePhase, lexicalPhase,
                semanticPhase, outputFilePath, errorLogFilePath, srcFilePath, printAST);


//        TestAdmin admin;
//        try {
//            admin = new TestAdmin(options);
//            admin.compile();
//            admin.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if( lexicalPhase )
            System.out.println("Scanner tests results: " + TestScanner.runAll(options));

        if( parsePhase )
            System.out.println("\nParser tests results: " + ParserTest.runAll(options));


    }
}
