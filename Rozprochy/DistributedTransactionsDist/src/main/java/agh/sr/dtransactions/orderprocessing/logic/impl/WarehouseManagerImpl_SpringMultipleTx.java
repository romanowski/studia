package agh.sr.dtransactions.orderprocessing.logic.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.Transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderWarhouseException;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService;

public class WarehouseManagerImpl_SpringMultipleTx implements
		WarehouseManagerService {

	ProductDao dao;
	PlatformTransactionManager mgr;

	public WarehouseManagerImpl_SpringMultipleTx(ProductDao productDao,
			PlatformTransactionManager springTransactionManager) {
		dao = productDao;
		mgr = springTransactionManager;
	}

	public void logLocationAndTotalPrice(Customer customer, int totalPrice)
			throws SQLException {
		TransactionStatus status = null;
		try {
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			status = mgr.getTransaction(def);
			dao.logLocationAndTotalPrice(customer, totalPrice);
			mgr.commit(status);
		} catch (Exception e) {
			try {
				mgr.rollback(status);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	@Override
	public int prepareProductsForShipment(Order order, Customer customer)
			throws OrderWarhouseException {

		TransactionStatus status = null;

		try {
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			status = mgr.getTransaction(def);

			if (!dao.checkIfProductsAvailable(order)) {
				throw new OrderWarhouseException("now products!");
			}

			int totalPrice = dao.calculateProductsTotalPrice(order);

			logLocationAndTotalPrice(customer, totalPrice);

			if (totalPrice > customer.getBalance()) {
				throw new OrderWarhouseException("no chash!");
			}

			dao.decreaseProductsAmounts(order);

			mgr.commit(status);

			return totalPrice;

		} catch (Exception e) {
			try {
				mgr.rollback(status);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			throw new OrderWarhouseException(e);
		}
	}

	@Override
	public void setProductDao(ProductDao productDao) {

		this.dao = productDao;

	}

}
