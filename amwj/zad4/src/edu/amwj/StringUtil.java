package edu.amwj;

public class StringUtil {

    static {
        System.loadLibrary("StringUtil");
    }

    private StringUtilStats stats = new StringUtilStats();

    native public String replace(String a, String b, String c);

    native public StringUtilStats getStats();

    private String inner_replace(String a, String b, String c) {
        stats.nextUse();
        int index = 0;
        while ((index = a.indexOf(b, index) + 1) > 0) {
            stats.nextRepl();
        }
        return a.replaceAll(b, c);
    }


    void ala() {
        stats.getNumberOfCalls();
    }
}
