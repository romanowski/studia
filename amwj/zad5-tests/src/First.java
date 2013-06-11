public class First {
    public static void main(String... args) {
        System.out.println("ala ma kota!");
        Second second = new Second();
        second.incCounter();
        second.doubleInc();
        second.incCounter();
        int counter = second.getCounter();
        System.out.println(counter + " ala ma kota!");
    }
}