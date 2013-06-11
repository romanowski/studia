package agh.sr.dtransactions.test;

import java.sql.SQLException;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;

public class ProductDao_ExOnLogging extends ProductDao {

	public ProductDao_ExOnLogging(DataSource ds) {
		super(ds);
	}

	@Override
	public void logLocationAndTotalPrice(Customer customer, int totalPrice)
			throws SQLException {
		throw new RuntimeException(
				"Simulating exception which should trigger rollback");
	}

}
