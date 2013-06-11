package agh.sr.dtransactions.orderprocessing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.util.Assert;

import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderItem;

public class ProductDao {

	protected DataSource ds;

	public ProductDao(DataSource ds) {
		this.ds = ds;
	}

	public void logLocationAndTotalPrice(Customer customer, int totalPrice)
			throws SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			PreparedStatement u = conn
					.prepareStatement("INSERT INTO product_audit( address, total_price) VALUES ( ? , ? )");
			u.setString(1, customer.getAddress());
			u.setInt(2, totalPrice);
			int rowCount = u.executeUpdate();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkIfProductsAvailable(Order order) throws SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
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
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int calculateProductsTotalPrice(Order order) throws SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
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
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void decreaseProductsAmounts(Order order) throws SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			PreparedStatement q = conn
					.prepareStatement("SELECT amount FROM product WHERE id = ?");
			PreparedStatement u = conn
					.prepareStatement("UPDATE product SET amount = ? WHERE id = ?");
			for (OrderItem orderItem : order.getOrderItems()) {
				q.setInt(1, orderItem.getProductId());
				ResultSet r = q.executeQuery();
				if (r.next()) {
					int amount = r.getInt(1);
					amount -= orderItem.getAmount();
					Assert.isTrue(amount >= 0);
					u.setInt(1, amount);
					u.setInt(2, orderItem.getProductId());
					u.executeUpdate();
				} else {
					throw new IllegalArgumentException("Cannot find product "
							+ orderItem.getProductId());
				}
				r.close();
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}