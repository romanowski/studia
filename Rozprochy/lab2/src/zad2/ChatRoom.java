package zad2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ChatRoom extends Remote {

	public Collection<ChatListener> getGuests() throws RemoteException;
	
	public String getID() throws RemoteException;

	public void registerClient(ChatListener listener) throws ChatException,
			RemoteException;

	public void leave(ChatListener listener) throws ChatException,
			RemoteException;

}
