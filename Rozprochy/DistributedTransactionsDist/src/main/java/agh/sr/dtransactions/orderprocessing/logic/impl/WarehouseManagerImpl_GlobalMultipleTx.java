package agh.sr.dtransactions.orderprocessing.logic.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderWarhouseException;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService;

public class WarehouseManagerImpl_GlobalMultipleTx implements
		WarehouseManagerService {

	ProductDao dao;
	UserTransaction transaction;
	TransactionManager mgr;

	public WarehouseManagerImpl_GlobalMultipleTx(ProductDao productDao,
			UserTransaction userTransaction,
			TransactionManager transactionManager) {

		this.dao = productDao;
		this.transaction = userTransaction;
		this.mgr = transactionManager;

	}

	public void logLocationAndTotalPrice(Customer customer, int totalPrice)
			throws SQLException {
		Connection conn = null;
		try {
			transaction.begin();
			dao.logLocationAndTotalPrice(customer, totalPrice);
			transaction.commit();
		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	@Override
	public int prepareProductsForShipment(Order order, Customer customer)
			throws OrderWarhouseException {

		try {
			transaction.begin();

			if (!dao.checkIfProductsAvailable(order)) {
				throw new OrderWarhouseException("now products!");
			}

			int totalPrice = dao.calculateProductsTotalPrice(order);

			Transaction t = mgr.suspend();
			logLocationAndTotalPrice(customer, totalPrice);
			mgr.resume(t);

			if (totalPrice > customer.getBalance()) {
				throw new OrderWarhouseException("no chash!");
			}

			dao.decreaseProductsAmounts(order);

			transaction.commit();

			return totalPrice;

		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			throw new OrderWarhouseException(e);
		}
	}

	@Override
	public void setProductDao(ProductDao productDao) {
		dao = productDao;
	}

}
