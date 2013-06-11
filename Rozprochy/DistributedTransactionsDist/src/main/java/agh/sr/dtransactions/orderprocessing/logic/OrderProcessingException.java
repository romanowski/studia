package agh.sr.dtransactions.orderprocessing.logic;

public class OrderProcessingException extends Exception {

	public OrderProcessingException(String msg) {
		super(msg);
	}

	public OrderProcessingException(Throwable e) {
		super(e);
	}

	public OrderProcessingException(String msg, Throwable e) {
		super(msg, e);
	}

}
