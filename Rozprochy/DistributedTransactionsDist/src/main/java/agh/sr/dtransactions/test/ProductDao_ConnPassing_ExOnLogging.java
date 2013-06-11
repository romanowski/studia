package agh.sr.dtransactions.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import agh.sr.dtransactions.orderprocessing.dao.connpassing.ProductDao_ConnPassing;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class ProductDao_ConnPassing_ExOnLogging extends ProductDao_ConnPassing {
	
	@Override
	public void logLocationAndTotalPrice(Customer customer, int totalPrice,
			Connection conn) throws SQLException {
		throw new RuntimeException(
				"Simulating exception which should trigger rollback");		
	}

}
