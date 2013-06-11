import edu.amwj.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 06.12.12
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] args) {

        String base = "ala ma kota. ala lubi koty";
        String b = "ala";
        String c = "ola";
        StringUtil n = new StringUtil();
        String out = n.replace(base, b, c);

        if (!base.replaceAll(b, c).equals(out)) {
            System.out.println("bad: " + out + "!");
        } else {
            System.out.println("OK");
        }

        try {
            n.replace(null, null, null);
            System.out.println("ERROR");
        } catch (IllegalArgumentException e) {
            System.out.println("OK");
        }
         System.out.println("DONE");

        System.out.println(n.getStats().getNumberOfCalls() + " " + n.getStats().getNumberOfReplacements());

    }

}
