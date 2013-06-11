package test;

public class Test5 {

	public static boolean endsWith(String k, String p){
		boolean t = k.endsWith(p);
		return t;
	}
	
	public static void main(String[] args){
		Test5.endsWith("krokodyl","dyl");
	}
}
