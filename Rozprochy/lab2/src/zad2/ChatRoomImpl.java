package zad2;	

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChatRoomImpl implements ChatRoom {

	transient Set<ChatListener> listeners = new HashSet<ChatListener>();

	String id;

	public ChatRoomImpl(String id) {
		this.id = id;
	}

	@Override
	public String getID() throws RemoteException {
		return id;
	}

	@Override
	public void registerClient(ChatListener listener) throws ChatException,
			RemoteException {

		if (listeners.contains(listener)) {
			throw new ChatException("room contains this listener");
		}

		listeners.add(listener);
	}

	@Override
	public void leave(ChatListener listener) throws ChatException,
			RemoteException {
		if (!listeners.contains(listener)) {
			throw new ChatException("room not  contains this listener");
		}

		listeners.remove(listener);
	}

	
	//safe due to serialization
	@Override
	public Collection<ChatListener> getGuests() {
		return listeners;
	}

}
