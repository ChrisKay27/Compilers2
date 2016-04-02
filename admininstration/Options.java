package admininstration;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Chris on 1/19/2016.
 */
public class Options {

    public final boolean quiet;
    public final boolean verbose;
    public boolean tuplePhase;
    public boolean parserPhase;
    public boolean fullCompile;
    public boolean lexicalPhase;
    public boolean semanticPhase;
    public final String outputFilePath;
    public final String errorFilePath;
    public String inputFilePath;
    public final boolean printAST;
    public boolean unitTesting;

    public Options(boolean quiet, boolean verbose, boolean tuplePhase, boolean parserPhase, boolean fullCompile, boolean lexicalPhase, boolean semanticPhase, String outputFilePath, String errorFilePath, String inputFilePath, boolean printAST) {
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
        this.printAST = printAST;
    }

    public void setPhase(int n) {
        this.lexicalPhase = false;
        this.parserPhase = false;
        this.semanticPhase = false;
        this.tuplePhase = false;
        this.fullCompile = false;
        switch (n) {
            case 1:
                this.lexicalPhase = true;
                break;
            case 2:
                this.parserPhase = true;
                break;
            case 3:
                this.semanticPhase = true;
                break;
            case 4:
                this.tuplePhase = true;
                break;
            case 5:
                this.fullCompile = true;
                break;
            default:
                throw new NotImplementedException();
        }
    }

    public String getPhase() {
        if(fullCompile){
            return "Complete Compilation Phase";
        }
        else if(tuplePhase){
            return "Tuple Phase";
        }
        else if(semanticPhase){
            return "Semantic Phase";
        }
        else if(parserPhase){
            return "Parser Phase";
        } else if (lexicalPhase) {
            return "Lexical Phase";
        } else {
            return "Phase not specified";
        }

    }
}
