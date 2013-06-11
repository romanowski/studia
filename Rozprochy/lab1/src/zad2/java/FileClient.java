package zad2.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileClient {

	private static final int bufSize = 1024;
	private static final int maxRetCount = 5;

	private static void usage() {
		System.out.println("Usage: <host> <port> <file>");
	}

	public static void main(String[] args) {

		if (args.length < 3) {
			usage();
			return;
		}

		File f = new File(args[2]);
		if (!f.exists() || !f.isFile()) {
			System.out.println("file dont exists or isnt regural file!");
			return;
		}

		Socket s = null;
		InputStream fileInput = null;
		byte[] buffer = new byte[bufSize];
		try {
			s = new Socket(args[0], new Integer(args[1]));
			OutputStream socketOut = s.getOutputStream();
			
			fileInput = new FileInputStream(f);
			int readed;
			while((readed = fileInput.read(buffer, 0, bufSize)) > 0){
				retLoop:
				for (int i = 0; i < maxRetCount; i++) {
					try{
						System.out.println("writing " + readed + " bytes.");
						socketOut.write(buffer, 0, readed);
						break retLoop;
					}
					catch (Exception e) {
						if(i == maxRetCount -1){
							e.printStackTrace();
							System.out.println("Error when sending file, abort!");
							return;
						}
					}
				}
			}
			socketOut.close();
			System.out.println("File send.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//sprzatanie
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
