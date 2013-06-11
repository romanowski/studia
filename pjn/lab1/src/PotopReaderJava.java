import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PotopReaderJava {
    public static void main(String[] args) throws IOException {
        long t1 = new Date().getTime();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("potop.txt"), "ISO-8859-2"));

        Map<String, Integer> occ = new HashMap<String, Integer>();

        Pattern p = Pattern.compile("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+");
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher m = p.matcher(line);
            while (m.find()) {
                String stem = stem(m.group());
                Integer count = occ.get(stem);
                if (count == null) {
                    count = 0;
                }
                count++;
                occ.put(stem, count);
            }
        }
        Map<Integer, List<String>> score = new TreeMap<Integer, List<String>>();
        for (String w : occ.keySet()) {
            Integer val = occ.get(w);
            List<String> ss = score.get(val);
            if (ss == null) {
                ss = new ArrayList<String>();
            }
            ss.add(w);
            score.put(val, ss);
        }
        long t2 = new Date().getTime();

        for (Integer i : score.keySet()) {
            System.out.print(i + ": ");
            for (String w : score.get(i)) {
                System.out.print(w + " ");
            }
            System.out.println("");
        }
        System.out.println("loaded in " + (t2 - t1) + " millis");
    }

    private static String stem(String w) {
        return w.toLowerCase();
    }
}
