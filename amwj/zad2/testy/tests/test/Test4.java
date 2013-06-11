package test;

public class Test4 {
	
	private String ins;
	
	public Test4(String s){
		ins = s;
	}
	
	private void concat(String p){
		ins = ins.concat(p);
	}
	
	public void duplicate(){
		concat(ins);
	}
	
	public int pow(int n){
		for(int i = 0; i < n; i++)
			duplicate();
		return ins.length();
	}
	
	public static void main(String[] args){
		Test4 tst = new Test4("a");
		int n = tst.pow(3);
	}

}
