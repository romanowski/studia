package agh.sr.dtransactions.test;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.InvoiceDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;

public class InvoiceDao_ExOnCreate extends InvoiceDao {

	public InvoiceDao_ExOnCreate(DataSource ds) {
		super(ds);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createInvoiceForCustomer(Customer customer, int totalPrice, Order o) {
		throw new RuntimeException(
				"Simulating exception which should trigger rollback");
	}

}
