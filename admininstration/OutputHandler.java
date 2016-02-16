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
    private Map<String,String> scannerOutput = new HashMap<>();
    private Map<String,String> parseOutput = new HashMap<>();


    private Consumer<String> out;

    public OutputHandler(Consumer<String> out) {
        this.out = out;
    }

    public void setOutput(Consumer<String> out) {
        this.out = out;
    }

    public void print(String msg) {
        out.accept(msg + "\n");
    }

//    public void addScannerOutput
}

