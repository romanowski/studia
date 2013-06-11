package agh.sr.dtransactions.test;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class ProductDao_ExOnDecrease extends ProductDao {

	public ProductDao_ExOnDecrease(DataSource ds) {
		super(ds);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void decreaseProductsAmounts(Order order) {
		throw new RuntimeException(
				"Simulating exception which should trigger rollback");
	}

}
