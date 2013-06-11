package agh.sr.dtransactions.orderprocessing.logic;

public class OrderWarhouseException extends Exception {
	
	public OrderWarhouseException(String msg) {
		super(msg);
	}

	public OrderWarhouseException(Throwable e) {
		super(e);
	}

	public OrderWarhouseException(String msg, Throwable e) {
		super(msg, e);
	}
}
