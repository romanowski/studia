package zad1;

public class ExecuteException extends Exception {

	private static final long serialVersionUID = -7194326621516293926L;

	public ExecuteException(String id, Throwable th) {
		super("when executing: " + id, th);
	}

	public ExecuteException(String msg) {
		super(msg);
	}

}
