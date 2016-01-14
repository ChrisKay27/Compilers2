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
    public static final String TRACE = "-t";


    public static void main(String[] args) {

        String srcFilePath = null;
        String errorLogFilePath = null;
        boolean trace = false;
        try {
            errorLogFilePath = getErrorLogFilePath(args);
            srcFilePath = getInputFilePath(args);
            trace = isTraceEnabled(args);
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
                admin = new Administration(srcFilePath,errorLogFilePath, trace);
                admin.compile();
                admin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //}
    }


    public static String getInputFilePath(String[] args){
        for (int i = 0; i < args.length; i++) {
            String s = args[i];

            if (PATH.equals(s))
                return args[++i];
        }
        return null;
    }
    public static String getErrorLogFilePath(String[] args){
        for (int i = 0; i < args.length; i++) {
            String s = args[i];

            if (ERR.equals(s))
                return args[++i];
        }
        return null;
    }
    public static boolean isTraceEnabled(String[] args){
        for (String s : args)
            if (TRACE.equals(s))
                return true;
        return false;
    }
}
