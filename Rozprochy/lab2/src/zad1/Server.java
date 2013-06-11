package zad1;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {

	public static void main(String[] args) {


		try {

			String path = "localhost";

			if (args.length > 0) {
				path = args[0];
			}

			TaskServerImpl server = new TaskServerImpl();
			TaskServer stub = (TaskServer) UnicastRemoteObject.exportObject(
					server, 0);

			Registry registry = LocateRegistry.getRegistry(path, 1099);
			registry.rebind(TaskServer.name, stub);

			System.out.println("Task server bound!");
		} catch (Exception e) {
			System.err.println("Task server exception:");
			e.printStackTrace();
		}
	}
}
