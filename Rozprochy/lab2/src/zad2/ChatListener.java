package zad2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatListener extends Remote{

	public String getNick() throws RemoteException;

	public void recieveMessage(String message, ChatListener from)  throws RemoteException;

}
