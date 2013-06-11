package agh.sr.dtransactions.orderprocessing.dao.idempotent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.InvoiceDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class InvoiceDao_Idempotent extends InvoiceDao {

	public InvoiceDao_Idempotent(DataSource ds) {
		super(ds);
	}

	@Override
	public void createInvoiceForCustomer(Customer customer, int totalPrice, Order order)
			throws SQLException {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			PreparedStatement q = conn
					.prepareStatement("SELECT id FROM orders WHERE id = ?");
			q.setString(1, order.getOrderId());
			ResultSet s = q.executeQuery();
			if (s.next()) {
				// It means that this order was already processed therefore it
				// can be ignored.
			} else {
				conn.close();
				super.createInvoiceForCustomer(customer, totalPrice, order);
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
