package Main;

import admininstration.Administration;
import testCases.Test;

import java.util.Arrays;

import static admininstration.Administration.debug;

/**
 * Compilers
 *
 * @Author Carston
 * Date -  1/13/2016.
 */
public class Main {

    public static final String ERR = "-err";
    public static final String PATH = "-c";

    public static void main(String[] args) {

//        if (debug()) { // RUN PREBUILT TEST CASES
//            //Test.init();
//            System.out.println("Tests results: " + Test.runAll());
//        } else { // RUNS WITH COMMAND LINE ARGUMENTS
        String srcFilePath = null;
        String errorLogFilePath = null;
        for(int i=0;i < args.length; i++){
            String s = args[i];

            if(ERR.equals(s)){
                errorLogFilePath = args[++i];
            }
            else if(PATH.equals(s)){
                srcFilePath = args[++i];
            }
        }

        if( srcFilePath == null ){
            System.err.println("No path specified, you must include -c Path/To/File.cs16");
        }
        else {
            Administration admin;
            try {
                admin = new Administration(srcFilePath,errorLogFilePath);
                admin.compile();
                admin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //}
    }
}
