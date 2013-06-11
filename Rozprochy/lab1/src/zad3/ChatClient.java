package zad3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {

	private static final String host = "224.0.0.0";

	public static InetAddress hostAddress;

	static {
		try {
			hostAddress = Inet4Address.getByName(host);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final int port = 9989;
	public static final int maxErrorCount = 16;

	public static void main(String[] args) {

		final MulticastSocket socket;

		try {
			socket = new MulticastSocket(port);
			socket.joinGroup(hostAddress);

			// nasłuciwanie na wiadomosci
			final Thread mainThread = Thread.currentThread();
		

			Scanner scaner = new Scanner(System.in);

			System.out.println("Podaj swoj nick:");
			final String author;
			String tmpAuthor = "";

			while (!UDPChatManager.checkAuthor((tmpAuthor = scaner.nextLine()))) {
				printAuthorHelp();
			}

			author = tmpAuthor;

			String readed;

			System.out.println("Start chatting!");

			Thread listener = new Thread() {

				byte[] buf = new byte[UDPChatManager.bufLenght];

				UDPChatManager manager = new UDPChatManager();

				int errorCount = 0;

				public void run() {
					while (true) {
						DatagramPacket packet = new DatagramPacket(buf,
								UDPChatManager.bufLenght);
						try {
							socket.receive(packet);
							ChatMessage msg = manager.manageDatagram(packet);
							if (msg != null) {
								if(!msg.author.equals(author)){
									printMessage(msg);
								}
							}

						} catch (IOException e) {
							e.printStackTrace();
							if (errorCount++ > maxErrorCount) {
								// to much errors
								mainThread.interrupt();
								return;
							}
						}
					}
				};
			};
			listener.start();

			while (!(readed = scaner.nextLine()).equals("\\exit")) {
				if (readed.equals("\\help")) {
					printHelp();
				} else {
					for (DatagramPacket packet : UDPChatManager
							.sendMessage(new ChatMessage(readed, author))) {
						socket.send(packet);
					}
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void printMessage(ChatMessage data) {
		System.out.println(data.getAuthor() + ":\n" + data.getMessage());
	}

	public static void printHelp() {
		System.out.println("help!");
	}

	public static void printAuthorHelp() {
		System.out.println("name is too long!");
	}

}