package zad2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import zad1.TaskServerImpl;

public class Server {
	public static void main(String[] args) {

		try {

			String path = "localhost";

			if (args.length > 0) {
				path = args[0];
			}

			ChatServerRmi server = new ChatServerRmi();

			Registry registry = LocateRegistry.getRegistry(path, 1099);
			registry.rebind(ChatServer.name,
					UnicastRemoteObject.exportObject(server, 0));

			System.out.println("chat manager bound!");
		} catch (Exception e) {
			System.err.println("chat mgr exception:");
			e.printStackTrace();
		}
	}
}
