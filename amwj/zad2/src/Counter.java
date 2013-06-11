import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 15.11.12
 * Time: 19:58
 * To change this template use File | Settings | File Templates.
 */
public class Counter {

    public static Map<String, Integer> instructionMap = new TreeMap<String, java.lang.Integer>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                synchronized (instructionMap) {
                    for (String insName : instructionMap.keySet()) {
                        Integer count = instructionMap.get(insName);
                        if (count >= 3) {
                            System.out.println(insName + "    " + count);
                        }
                    }
                }

            }
        });
    }

    public static void put(String ins) {
        synchronized (instructionMap) {
            instructionMap.put(ins, instructionMap.get(ins) != null ? instructionMap.get(ins) + 1 : 1);
        }
    }

    public static final String putName = "put";
    public static final String putSignature = "(Ljava/lang/String;)V";


}
