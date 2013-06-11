package edu.amwj;

public class StringUtilStats {

    private int useCount = 0;
    private int replCount = 0;

    native public int getNumberOfCalls();

    native public int getNumberOfReplacements();

    synchronized void nextUse() {
        useCount++;
    }

    synchronized void nextRepl() {
        replCount++;
    }

}
