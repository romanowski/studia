package agh.sr.dtransactions.orderprocessing.logic;

import java.util.List;

public class Order {

	private String orderId;

	private int customerId;

	private List<OrderItem> orderItems;

	public Order(String orderId, int customerId, List<OrderItem> orderItems) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.orderItems = orderItems;
	}

	public String getOrderId() {
		return orderId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

}
