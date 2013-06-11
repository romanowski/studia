package zad2;

import java.io.Reader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

	static void usage() {
		System.out.println("Usage: host nick topic");
	}

	public static void main(String[] args) {

		if (args.length < 3) {
			usage();
			return;
		}
		String host = args[0];
		String nick = args[1];
		String topic = args[2];

		try {

			Registry registry = LocateRegistry.getRegistry(host, 1099);

			for (String string : registry.list()) {
				System.out.println(string);
			}

			ChatServer server = (ChatServer) registry.lookup(ChatServer.name);

			SingleRoomListener listener = new SingleRoomListener(nick, topic,
					server);

			System.out.println("type \\q to quit");

			Scanner s = new Scanner(System.in);

			String message;
			while (!(message = s.nextLine()).equals("\\q")) {
				listener.sendMessage(message);
			}

			listener.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
