package agh.sr.dtransactions.orderprocessing.dao.connpassing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.util.Assert;

import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderProcessingException;

public class CustomerDao_ConnPassing {

	public Customer getCustomerDetails(Order order, Connection conn)
			throws OrderProcessingException, SQLException {
		PreparedStatement customerQuery = conn
				.prepareStatement("SELECT id, first_name, last_name, address, transaction_cnt FROM customer WHERE id = ?");
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
	}

	public void reduceCustomerBalance(Customer customer, Connection conn)
			throws SQLException {
		PreparedStatement customerUpdate = conn
				.prepareStatement("UPDATE customer SET balance = ? WHERE id = ?");
		customerUpdate.setInt(1, customer.getBalance());
		customerUpdate.setInt(2, customer.getId());
		int updatedRows = customerUpdate.executeUpdate();
		Assert.isTrue(updatedRows == 1);
	}
}
