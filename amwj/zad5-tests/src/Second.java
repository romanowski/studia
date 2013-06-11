public class Second {
    public int counter = 0;

    public void incCounter() {
        counter++;
    }

    public void doubleInc() {
        incCounter();
        incCounter();
    }

    public int getCounter() {
        return counter;
    }
}