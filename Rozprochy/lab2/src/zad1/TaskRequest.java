package zad1;

import java.io.Serializable;

public interface TaskRequest<T> extends Serializable {
	public T execute();
}
