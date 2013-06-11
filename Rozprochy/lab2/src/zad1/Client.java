package zad1;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Client {

	private static Map<String, TaskRequest<?>> tasks = new HashMap<String, TaskRequest<?>>();
	static {
		TaskRequest<Long> sleeps2 = new TaskRequest<Long>() {

			@Override
			public Long execute() {
				long time = System.nanoTime();

				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return System.nanoTime() - time;
			}

		};
		tasks.put("sleep2", sleeps2);

		TaskRequest<Long> sleeps1 = new TaskRequest<Long>() {

			@Override
			public Long execute() {
				long time = System.nanoTime();

				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return System.nanoTime() - time;
			}

		};

		tasks.put("sleep1", sleeps1);

		TaskRequest<String> error = new TaskRequest<String>() {

			@Override
			public String execute() {
				throw new RuntimeException("Sample Exeption!");
			}

		};

		tasks.put("error", error);

	}

	public static void help() {
		System.out.println("Usage: [host task]");
		System.out.println("defalult loclahost sleep2");
		System.out.println("Possible taks:");
		for (String tas : tasks.keySet()) {
			System.out.println("tas");
		}
	}

	public static void main(String args[]) {
		try {
			String name = TaskServer.name;
			String task = "sleep2";

			String path;
			if (args.length > 1) {

				if (args[0].equals("help")) {
					help();
					return;
				}

				path = args[0];
				task = args[1];

				if (!tasks.containsKey(task)) {
					System.out.println("bad taks name: " + task);
					help();
					return;
				}

			} else {
				path = "localhost";
			}

			System.out.println("Connecting to " + path);

			Registry registry = LocateRegistry.getRegistry(path, 1099);

			TaskServer taskServer = (TaskServer) registry.lookup(name);

			try {
				TaskRequest<?> ct = tasks.get(task);

				System.out.println("Returned Value:"
						+ taskServer.runTask(ct, task));

			} catch (Exception e) {
				e.printStackTrace();
			}
			for (TaskInfo t : taskServer.getLastTask()) {
				System.out
						.println("Longest task:" + taskServer.getLongesTask());
				System.out.println(t);
				if (!t.success()) {
					System.out.println("Exception for task " + t.getId());
					System.out.println(t.getException());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
