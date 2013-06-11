
public class Watek extends Thread{

	public void run() {
		
		int a = 0;
		for(int i = 1; i < 10; i++){
			a++;
		}
		
	}
	
	public static void main(String[] args) {
		
		Thread t = new Watek();
		t.start();

	}

}
