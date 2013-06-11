
/**
 * Klasa testowa.
 */
public class Test0 {
    public String fool1(String fool) {
        return "some" + " text";
    }

    public Test0() {
        System.out.println(fool1("fool"));
    }
    
    public double getDouble() {
    	return getX() * getS().length();
    }
    
    public double getX() {
    	return 7.0;
    }
    
    public String getS() {
    	return "ala ma kota";
    }

    public static void main(String[] argv) {
    	Test0 t = new Test0();
    	System.out.println(t.getDouble());
    }
}
