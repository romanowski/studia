package zad1.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaClient {

	private static void usage() {
		System.out.println("usage: <address> <port> <lenght> <number>");
	}

	public static void main(String[] args) {

		if (args.length < 2) {
			usage();
			return;
		}

		int port = new Integer(args[1]);

		String address = args[0];

		int lenght = new Integer(args[2]);

		long number;

		switch (lenght) {
		case 1:
			number = new Byte(args[3]);
			break;
		case 2:
			number = new Short(args[3]);
			break;
		case 4:
			number = new Integer(args[3]);
			break;
		case 8:
			number = new Long(args[3]);
			break;
		default:
			System.out.println("bad lenght supported ones: 1 2 4 8");
			return;
		}

		Socket s = null;

		try {
			s = new Socket(address, port);
			s.setSendBufferSize(lenght);	

			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			DataInputStream in = new DataInputStream(s.getInputStream());

			System.out.println("Send: " + number + " with lenght: "+ lenght);
			
			switch (lenght) {
			case 1:
				out.writeByte((int) number);
				number = in.readByte();
				break;
			case 2:
				out.writeShort((int) number);
				number = in.readShort();
				break;
			case 4:
				out.writeInt((int) number);
				number = in.readInt();
				break;
			case 8:
				out.writeLong(number);
				number = in.readLong();
				break;
			default:
				// this code never happens.
				break;
			}

			System.out.println("Got: " + number);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}
