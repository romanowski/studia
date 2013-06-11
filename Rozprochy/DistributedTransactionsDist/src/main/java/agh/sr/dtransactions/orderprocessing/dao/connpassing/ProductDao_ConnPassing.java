package agh.sr.dtransactions.orderprocessing.dao.connpassing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.util.Assert;

import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderItem;

public class ProductDao_ConnPassing {

	public void logLocationAndTotalPrice(Customer customer, int totalPrice,
			Connection conn) throws SQLException {
		PreparedStatement u = conn
				.prepareStatement("INSERT INTO product_audit( address, total_price) VALUES ( ? , ? )");
		u.setString(1, customer.getAddress());
		u.setInt(2, totalPrice);
		int rowCount = u.executeUpdate();
	}

	public boolean checkIfProductsAvailable(Order order, Connection conn)
			throws SQLException {
		boolean productsAvailable = true;
		PreparedStatement q = conn
				.prepareStatement("SELECT amount FROM product WHERE id = ?");
		for (OrderItem orderItem : order.getOrderItems()) {
			q.setInt(1, orderItem.getProductId());
			ResultSet r = q.executeQuery();
			if (r.next()) {
				int amount = r.getInt(1);
				if (amount < orderItem.getAmount()) {
					return false;
				}
			} else {
				throw new IllegalArgumentException("Cannot find product "
						+ orderItem.getProductId());
			}

		}
		return productsAvailable;
	}

	public int calculateProductsTotalPrice(Order order, Connection conn)
			throws SQLException {
		int totalPrice = 0;
		PreparedStatement q = conn
				.prepareStatement("SELECT price FROM product WHERE id = ?");
		for (OrderItem orderItem : order.getOrderItems()) {
			q.setInt(1, orderItem.getProductId());
			ResultSet r = q.executeQuery();
			if (r.next()) {
				int price = r.getInt(1);
				totalPrice += price;
			} else {
				throw new IllegalArgumentException("Cannot find product "
						+ orderItem.getProductId());
			}
		}
		return totalPrice;
	}

	public void decreaseProductsAmounts(Order order, Connection conn)
			throws SQLException {
		PreparedStatement q = conn.prepareStatement(
				"SELECT amount FROM product WHERE id = ?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		for (OrderItem orderItem : order.getOrderItems()) {
			q.setInt(1, orderItem.getProductId());
			ResultSet r = q.executeQuery();
			if (r.next()) {
				int amount = r.getInt(1);
				amount -= orderItem.getAmount();
				Assert.isTrue(amount >= 0);
				r.updateInt(1, amount);
				r.updateRow();
			} else {
				throw new IllegalArgumentException("Cannot find product "
						+ orderItem.getProductId());
			}
			r.close();
		}
	}
}
