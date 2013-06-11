
public class Obiekt {

	public void test() {
	//	System.out.println("ala: " + metoda());
	}
	
	public Obiekt metoda() {
		return new Obiekt();
	}
	
	public static void main(String[] args) {
		Obiekt o = new Obiekt();
		o.test();
	}

}
