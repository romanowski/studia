package agh.sr.dtransactions.test;

import java.sql.Connection;

import agh.sr.dtransactions.orderprocessing.dao.connpassing.ProductDao_ConnPassing;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class ProductDao_ConnPassing_ExOnDecrease extends ProductDao_ConnPassing {

	@Override
	public void decreaseProductsAmounts(Order order, Connection conn) {
		throw new RuntimeException(
				"Simulating exception which should trigger rollback");
	}

}
