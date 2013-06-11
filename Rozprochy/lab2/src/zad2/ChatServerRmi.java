package zad2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ChatServerRmi implements ChatServer {

	Map<String, ChatRoom> rooms = new HashMap<String, ChatRoom>();

	@Override
	public ChatRoom joinChat(String topic, ChatListener listener)
			throws ChatException, RemoteException {

		ChatRoom room;

		if (!rooms.containsKey(topic)) {
			room = (ChatRoom) UnicastRemoteObject.exportObject(
					new ChatRoomImpl(topic), 0);
			rooms.put(topic, room);
		} else {
			room = rooms.get(topic);
		}
		room.registerClient(listener);

		return room;
	}

}
