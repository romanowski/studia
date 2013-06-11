package agh.sr.dtransactions.orderprocessing.dao.connpassing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class InvoiceDao_ConnPassing {

	public void createInvoiceForCustomer(Customer customer, int totalPrice,
			Connection conn) throws SQLException {
		PreparedStatement u = conn
				.prepareStatement("INSERT INTO invoice( customer_first_name, customer_last_name, customer_address, total_price) VALUES ( ? , ? , ? , ? )");
		u.setString(1, customer.getFirstName());
		u.setString(2, customer.getLastName());
		u.setString(3, customer.getAddress());
		u.setInt(4, totalPrice);
		int rowCount = u.executeUpdate();
	}
}
