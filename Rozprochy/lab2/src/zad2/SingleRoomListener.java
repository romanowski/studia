package zad2;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

public class SingleRoomListener implements ChatListener {

	String nick;
	transient ChatRoom room;
	transient ChatListener exported;

	//exportujemy naszego klienta
	public SingleRoomListener(String nick, String topic, ChatServer server)
			throws RemoteException, ChatException {

		this.nick = nick;
		exported = (ChatListener) UnicastRemoteObject.exportObject(this, 0);

		this.room = server.joinChat(topic, exported);
	}

	@Override
	public String getNick() {
		return nick;
	}

	@Override
	public void recieveMessage(String message, ChatListener from)
			throws RemoteException {

		System.out.println("New message from " + from.getNick());
		System.out.println(message);

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChatListener) {
			try {
				return nick.equals(((ChatListener) obj).getNick());
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return nick.hashCode();
	}

	public void sendMessage(String msg) throws RemoteException, ChatException {

		System.out.println("Do sprawdzenia gdzie sie wywoluje wysyłanie");

		Collection<ChatListener> listeners = room.getGuests();
		for (ChatListener chatListener : listeners) {
			if (!this.equals(chatListener))
				chatListener.recieveMessage(msg, exported);
		}

	}

	public void disconnect() throws RemoteException, ChatException {
		room.leave(exported);
		UnicastRemoteObject.unexportObject(this, true);
	}
}
