package zad2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {

	public static final String name = "ChatServer12";

	public ChatRoom joinChat(String topic, ChatListener listener)
			throws ChatException, RemoteException;

}
