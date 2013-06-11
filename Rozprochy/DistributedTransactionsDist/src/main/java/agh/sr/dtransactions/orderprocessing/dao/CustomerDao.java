package agh.sr.dtransactions.orderprocessing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.util.Assert;

import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderProcessingException;

public class CustomerDao {

	protected DataSource ds;
	
	public CustomerDao(DataSource ds) {
		this.ds = ds;
	}

	public Customer getCustomerDetails(Order order)
			throws OrderProcessingException, SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			PreparedStatement customerQuery = conn
					.prepareStatement("SELECT id, first_name, last_name, address, balance FROM customer WHERE id = ?");
			customerQuery.setInt(1, order.getCustomerId());
			ResultSet customerResult = customerQuery.executeQuery();
			if (customerResult.next()) {
				String firstName = customerResult.getString(2);
				String lastName = customerResult.getString(3);
				String address = customerResult.getString(4);
				int balance = customerResult.getInt(5);
				return new Customer(order.getCustomerId(), firstName, lastName,
						address, balance);
			} else {
				throw new IllegalArgumentException("Cannot find customer "
						+ order.getCustomerId());
			}
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void reduceCustomerBalance(Customer customer, Order order)
			throws SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			PreparedStatement customerUpdate = conn
					.prepareStatement("UPDATE customer SET balance = ? WHERE id = ?");
			customerUpdate.setInt(1, customer.getBalance());
			customerUpdate.setInt(2, customer.getId());
			int updatedRows = customerUpdate.executeUpdate();
			customerUpdate.close();
			Assert.isTrue(updatedRows == 1);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
