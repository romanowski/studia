package agh.sr.dtransactions.orderprocessing.dao.idempotent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class ProductDao_Idempotent extends ProductDao {

	public ProductDao_Idempotent(DataSource ds) {
		super(ds);
	}

	@Override
	public void decreaseProductsAmounts(Order order) throws SQLException {
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
				super.decreaseProductsAmounts(order);
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
