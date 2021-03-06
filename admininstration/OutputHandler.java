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
    private Map<String, String> scannerOutput = new HashMap<>();
    private Map<String, String> parseOutput = new HashMap<>();

    private List<String> errorLog = new ArrayList<>();

    private Consumer<String> resultOut;
    private Consumer<String> messageOut;
    private Consumer<String> errorOut;

    public OutputHandler(Consumer<String> messageOut) {
        this.setMaxErrors(10);
        this.messageOut = messageOut;
    }

    public void setErrorOutput(Consumer<String> errorOut) {
        this.errorOut = errorOut;
    }

    public void setResultOutput(Consumer<String> resultOut) {
        this.resultOut = resultOut;
    }

    public void setMaxErrors(int max) {
        this.MAX_ERRORS = max;
    }

    public void printErrorMessage(String msg) {
        if (errorOut != null)
            errorOut.accept(msg);
    }

    public void addErrorMessage(String errorMsg) {
        errorLog.add(errorMsg);
    }

    public void addScannerOutput(String currentLine, String output) {
        scannerOutput.put(currentLine, output);
        scannerReadLines.add(currentLine);
    }


    public void addParseOutput(String currentLine, String output) {
        String currentOutputForTheLine = parseOutput.get(currentLine);
        if (currentOutputForTheLine == null) {
            currentOutputForTheLine = "";
            parserReadLines.add(currentLine);
        }

        currentOutputForTheLine += output + '\n';
        parseOutput.put(currentLine, currentOutputForTheLine);
    }

    public void printOutputs(Consumer<String> out) {
        scannerReadLines.forEach(s -> {
            out.accept(s);
            out.accept(scannerOutput.get(s));
        });
    }

    public void printErrors() {
        printErrors(errorOut);
    }

    public void printScannerOutput(Consumer<String> out) {
        out.accept("\n\n------ Scanner Output -------\n\n");
        scannerReadLines.forEach(s -> {
            out.accept(s);
            out.accept(scannerOutput.get(s));
        });
    }

    public void printParserOutput(Consumer<String> out) {
        out.accept("\n\n------ Parser Output -------\n\n");
        parserReadLines.forEach(s -> {
            out.accept(s + '\n');
            out.accept(parseOutput.get(s) + '\n');
        });
    }

    /**
     * @param out used to change where the error output is going to
     */
    public void printErrors(Consumer<String> out) {
        if (errorLog.size() > MAX_ERRORS)
            errorLog = errorLog.subList(0, MAX_ERRORS);
        errorLog.forEach(out);
    }

    public List<String> getErrorLog() {
        return errorLog;
    }

}

