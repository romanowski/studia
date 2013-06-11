package test2.pack.pack2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class Test {

	public Test(){
		try{
			openFile();
		}catch(FileNotFoundException e){
			System.out.println("krowa:P");
		}
	}
	
	private void openFile() throws FileNotFoundException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("kumak")));
	}
	
	public static void main(String[] args){
		new Test();
	}
	
}
