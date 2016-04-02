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
            System.err.println("Error parsing parameters.");
            System.exit(2);
            return;
        }

        Options options = new Options(quietEnabled, verboseEnabled, tuplePhase, parsePhase, compilePhase, lexicalPhase,
                semanticPhase, outputFilePath, errorLogFilePath, srcFilePath, printAST);

        if (!lexicalPhase && !parsePhase && !semanticPhase && !tuplePhase)
            lexicalPhase = parsePhase = semanticPhase = tuplePhase = true;
        StringBuilder resultsString = new StringBuilder();
        boolean overallResults = true;
        if (lexicalPhase) {
            options.setPhase(1);
            boolean results = TestScanner.runAll(options);
            resultsString.append("Scanner tests results: " + results + "\n");
            overallResults = results;
        }
        if (parsePhase) {
            options.setPhase(2);
            boolean results = ParserTest.runAll(options);
            resultsString.append("Parser tests results: " + results + "\n");
            overallResults &= results;
        }
        if (semanticPhase) {
            options.setPhase(3);
            boolean results = SemanticAnalyzerTesting.runAll(options);
            resultsString.append("Semantic tests results: " + results + "\n");
            overallResults &= results;
        }
        if (tuplePhase) {
            options.setPhase(4);
            boolean results = QuadGenTester.runAll(options);
            resultsString.append("Code Generation tests results: " + results + "\n");
            overallResults &= results;
        }
        System.out.println("\n" + resultsString.toString());
        System.out.println("Overall Test Results: " + overallResults);
    }
}
