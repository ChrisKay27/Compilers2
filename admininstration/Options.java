package admininstration;

/**
 * Created by Chris on 1/19/2016.
 */
public class Options {

    public final boolean quiet;
    public final boolean verbose;
    public final boolean tuplePhase;
    public final boolean parserPhase;
    public final boolean fullCompile;
    public final boolean lexicalPhase;
    public final boolean semanticPhase;
    public final String outputFilePath;
    public final String errorFilePath;
    public String inputFilePath;


    public Options(boolean quiet, boolean verbose, boolean tuplePhase, boolean parserPhase, boolean fullCompile, boolean lexicalPhase, boolean semanticPhase, String outputFilePath, String errorFilePath, String inputFilePath) {
        this.quiet = quiet;
        this.verbose = verbose;
        this.tuplePhase = tuplePhase;
        this.parserPhase = parserPhase;
        this.fullCompile = fullCompile;
        this.lexicalPhase = lexicalPhase;
        this.semanticPhase = semanticPhase;
        this.outputFilePath = outputFilePath;
        this.errorFilePath = errorFilePath;
        this.inputFilePath = inputFilePath;
    }
}
