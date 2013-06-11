package test.pack;

public class Test3 {
	
	public int add(int a, int b) {
		return a + b;
	}

	public int sqr(int a) {
		return a*a;
	}
	
	public static void main(String[] args){
		Test3 tst = new Test3();
		tst.add(1, 2);
		tst.sqr(3);
	}
	
}
