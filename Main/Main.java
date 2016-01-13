package Main;

import admininstration.Administration;
import testCases.Test;

import static admininstration.Administration.debug;

/**
 * Compilers
 *
 * @Author Carston
 * Date -  1/13/2016.
 */
public class Main {

    public static void main(String[] args) {

        if (debug()) { // RUN PREBUILT TEST CASES
            Test.init();
            Test.runAll();
        } else { // RUNS WITH COMMAND LINE ARGUMENTS
            if (args[0].equals("-c")) { // command line switch for compile option
                String path = args[1]; // command line argument for source code file path
                Administration admin;
                try {
                    admin = new Administration(path);
                    admin.compile();
                    admin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }
    }
}
