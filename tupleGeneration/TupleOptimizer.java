package tupleGeneration;

/**
 * Created by Chris on 3/31/2016.
 */
public class TupleOptimizer {
    public static String optimize(String code) {
//        if( true )
//            return code;
        String[] split = code.split("\n");

        boolean optimized = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < split.length; i++) {
            String t = split[i-1];
            String t2 = split[i];
            if( !t.startsWith("(asg") ) {
                sb.append(t).append('\n');
                continue;
            }

            String t2Operator = t2.substring(0,t2.indexOf(',')+1);

            t = t.substring(t.indexOf(',')+1,t.length()-1).replace(",-,",":");    //should be in the form (2,3)-(1,3) now
            t2 = t2.substring(t2.indexOf(',')+1,t2.length()-1).replace(",-,",":"); //should be in the form (1,3)-(2,4) now

            String[] tArgs = t.split(":");
            String[] t2Args = t2.split(":");

            if( tArgs[1].equals(t2Args[0]) ) {
                sb.append(t2Operator).append(tArgs[0]).append(",-,").append(t2Args[1]).append(")\n");
                i++;
                optimized = true;
            }
//            else if( tArgs[1].equals(t2Args[0]) ) {
//                sb.append(t2Operator).append(tArgs[0]).append(",-,").append(t2Args[1]).append(")\n");
//                i++;
//                optimized = true;
//            }
            else{
                sb.append(split[i-1]).append('\n');
                optimized = false;
            }
        }
        if( !optimized )
            sb.append(split[split.length-1]);

        return sb.toString();
    }



}
