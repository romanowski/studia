package agh.sr.dtransactions.orderprocessing.logic;

public class OrderItem {

	private int productId;

	private int amount;

	public OrderItem(int productId, int amount) {
		this.productId = productId;
		this.amount = amount;
	}

	public int getProductId() {
		return productId;
	}

	public int getAmount() {
		return amount;
	}

}
