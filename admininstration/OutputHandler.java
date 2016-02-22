package admininstration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Chris on 1/14/2016.
 */
public class OutputHandler {
    private int MAX_ERRORS;

    private List<String> scannerReadLines = new ArrayList<>();
    private List<String> parserReadLines = new ArrayList<>();
    private Map<String,String> scannerOutput = new HashMap<>();
    private Map<String,String> parseOutput = new HashMap<>();

    private List<String> errorLog = new ArrayList<>();

    private Consumer<String> out;
    private Consumer<String> errorOut;

    public OutputHandler(Consumer<String> out) {
        this.setMaxErrors(10);
        this.out = out;
    }

    public void setOutput(Consumer<String> out) {
        this.out = out;
    }
    public void setErrorOutput(Consumer<String> out) {
        errorOut = out;
    }

    public void setMaxErrors(int max){
        this.MAX_ERRORS = max;
    }

    public void print(String msg) {
        out.accept(msg + "\n");
    }

    public void printErrorMessage(String msg) {
        if( errorOut != null )
            errorOut.accept(msg);
        errorLog.add(msg);
    }

    public void addScannerOutput(String currentLine, String output) {
        scannerOutput.put(currentLine, output);
        scannerReadLines.add(currentLine);
    }

    public void printScannerOutput(Consumer<String> out){
        out.accept("\n\n------ Scanner Output -------\n\n");
        scannerReadLines.forEach(s -> {
            out.accept(s);
            out.accept(scannerOutput.get(s));
        });
    }

    public void addParseOutput(String currentLine, String output) {
        String currentOutputForTheLine = parseOutput.get(currentLine);
        if( currentOutputForTheLine == null ) {
            currentOutputForTheLine = "";
            parserReadLines.add(currentLine);
        }

        currentOutputForTheLine += output + '\n';
        parseOutput.put(currentLine, currentOutputForTheLine);
    }

    public void printParserOutput(Consumer<String> out){
        out.accept("\n\n------ Parser Output -------\n\n");
        parserReadLines.forEach(s -> {
            out.accept(s + '\n');
            out.accept(parseOutput.get(s) + '\n');
        });
    }

    public void printOutputs(Consumer<String> out) {
        scannerReadLines.forEach(s -> {
            out.accept(s);
            out.accept(scannerOutput.get(s));
        });
    }


    public void printErrorOutputs(){
        printErrorOutputs(errorOut);
    }

    /**
     * @param out used to change where the error output is going to
     */
    public void printErrorOutputs(Consumer<String> out){
        if (errorLog.size() > MAX_ERRORS)
            errorLog = errorLog.subList(0,9);
        errorLog.forEach(out::accept);
    }

    public List<String> getErrorLog() {
        return errorLog;
    }


//    public void addScannerOutput
}

