package zad1;

import java.io.Serializable;
import java.rmi.Remote;

public class TaskInfo implements Serializable {

	long executionTime;

	Throwable exception;

	String id;

	public TaskInfo(String id, long time, Throwable th) {
		this.id = id;
		this.executionTime = time;
		this.exception = th;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public Throwable getException() {
		return exception;
	}

	public String getId() {
		return id;
	}

	boolean success() {
		return exception == null;
	}

	@Override
	public String toString() {
		return String.format("Task %s executed in: %d with: %s", id,
				executionTime,
				success() ? "success" : exception.getLocalizedMessage());
	}
}
