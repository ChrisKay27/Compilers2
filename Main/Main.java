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

        String srcFilePath = null;
        String errorLogFilePath = null;

        try {
            for (int i = 0; i < args.length; i++) {
                String s = args[i];

                if (ERR.equals(s)) {
                    errorLogFilePath = args[++i];
                } else if (PATH.equals(s)) {
                    srcFilePath = args[++i];
                }
            }
        }
        catch(Exception e){
            System.err.println("Error parsing parameters, try -c Path/To/File.cs16 -err Path/To/FileLogFile.txt");
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
