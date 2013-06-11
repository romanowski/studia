package agh.sr.dtransactions.orderprocessing.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class InvoiceDao {

	protected DataSource ds;

	public InvoiceDao(DataSource ds) {
		this.ds = ds;
	}

	public void createInvoiceForCustomer(Customer customer, int totalPrice,
			Order order) throws SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			PreparedStatement u = conn
					.prepareStatement("INSERT INTO invoice( customer_first_name, customer_last_name, customer_address, total_price) VALUES ( ? , ? , ? , ? )");
			u.setString(1, customer.getFirstName());
			u.setString(2, customer.getLastName());
			u.setString(3, customer.getAddress());
			u.setInt(4, totalPrice);
			int rowCount = u.executeUpdate();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
