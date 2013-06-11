
public class Static2 {

	private static int field;
	private static String str;
	
	static {
		field = 12;
	}
	{
		str = "str_new";
	}
	
	public static int getField(){
		return field;
	}
	
	public int strLength(){
		return str.length();
	}
	
	public static void main(String[] args){
		
		int k = 100;
		while(k > 0){		
			Static st = new Static();
			st.strLength();
			Static.getField();
			k--;
		}
	}
	
}
