import java.io.IOException;


public class Test4 {
	private byte someByte = 45;

	private char someChar = 'A';

	public static void main(String[] argv) throws IOException {
		new Test4();
	}

	public Test4() {
		byte someByte = this.someByte;
		char someChar = this.someChar;
	}
}
