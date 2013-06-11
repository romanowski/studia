package zad1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

interface TaskServer extends Remote {

	public final String name = "TaskServer1";

	public <T> T runTask(TaskRequest<T> t, String taskId)
			throws RemoteException, ExecuteException;

	public List<TaskInfo> getLastTask() throws RemoteException;

	public TaskInfo getLongesTask() throws RemoteException;

	public TaskInfo taskInfo(String taskId) throws RemoteException;

}
