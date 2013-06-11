
public class Test2 {
	
	private int k;
	
	public Test2(){
		k = 5;
	}
	
	public int getK(){
		return k;
	}
	
	public static void echo(){
		Test2 tst = new Test2();
		System.out.println(tst.getK());
	}
	
	public static void main(String[] args){
		Test2.echo();
	}
	
}
