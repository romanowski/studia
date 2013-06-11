package zad1;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class TaskServerImpl implements TaskServer {

	public Map<String, TaskInfo> tasks = Collections
			.synchronizedMap(new HashMap<String, TaskInfo>());

	public ArrayBlockingQueue<TaskInfo> lastes = new ArrayBlockingQueue<TaskInfo>(
			10);

	public TaskServerImpl() {
	}

	private static TaskInfo stabInfo = new TaskInfo("", 0l, null);

	@Override
	public <T> T runTask(TaskRequest<T> task, String taskId)
			throws RemoteException, ExecuteException {

		if (tasks.containsKey(taskId)) {
			throw new ExecuteException("Task with given id exist");
		}
		tasks.put(taskId, stabInfo);

		long time = System.nanoTime();
		Throwable t = null;
		try {
			return task.execute();
		} catch (Exception e) {
			e.printStackTrace();
			t = e;
			throw new ExecuteException(taskId, e);
		} finally {
			TaskInfo info = new TaskInfo(taskId, System.nanoTime() - time, t);
			if (!lastes.offer(info)) {
				lastes.poll();
				lastes.offer(info);
			}
			tasks.put(taskId, info);
		}
	}

	@Override
	public List<TaskInfo> getLastTask() throws RemoteException {
		return new ArrayList<TaskInfo>(lastes);
	}

	@Override
	public TaskInfo getLongesTask() throws RemoteException {

		TaskInfo tmp = null;
		long time = -1;
		for (TaskInfo ti : tasks.values()) {
			if (ti.getExecutionTime() > time) {
				tmp = ti;
			}
		}
		return tmp;
	}

	@Override
	public TaskInfo taskInfo(String taskId) throws RemoteException {
		return tasks.get(taskId);
	}

}
