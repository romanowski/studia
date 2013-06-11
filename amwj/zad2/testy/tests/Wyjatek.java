
public class Wyjatek {

	public void wyjatek(int num) throws Exception {
		if (num == 1)
			throw new Exception("ex1");
		try {
			if (num == 2)
				throw new Exception("ex2");
			metoda();
			if (num == 3)
				throw new Exception("ex3");
			metoda();
			if (num == 4)
				throw new Exception("ex4");
		} catch (Exception e) {
		//	System.out.println("Miejsce1: " + e);
		}
		if (num == 5)
			throw new Exception("ex5");
	}
	
	public void metoda() {
	//	System.out.println("hello");
	}
	
	public static void main(String[] args) {
		for (int i=1; i<=5; ++i) {
			try {
				Wyjatek w = new Wyjatek();
				w.wyjatek(i);
			} catch (Exception e) {
		//		System.out.println("Miejsce2: " + e);
			}
		}
	}

}
