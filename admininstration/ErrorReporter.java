package admininstration;

import java.util.function.Consumer;

/**
 * Created by Chris on 1/14/2016.
 */
public class ErrorReporter {
    private Consumer<String> out;

    public ErrorReporter(Consumer<String> out) {
        this.out = out;
    }

    public void setOutput(Consumer<String> out) {
        this.out = out;
    }

    public void print(String msg) {
        System.out.println("Writing error: " + msg);
        out.accept(msg);
    }
}
