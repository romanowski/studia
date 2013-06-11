package agh.sr.dtransactions.orderprocessing.logic.impl;

import javax.transaction.Transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderWarhouseException;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService;

public class WarehouseManagerImpl_SpringTx implements WarehouseManagerService {

	ProductDao dao;
	PlatformTransactionManager mgr;

	public WarehouseManagerImpl_SpringTx(ProductDao productDao,
			PlatformTransactionManager springTransactionManager) {
		dao = productDao;
		mgr = springTransactionManager;
	}

	@Override
	public int prepareProductsForShipment(Order order, Customer customer)
			throws OrderWarhouseException {

		TransactionStatus status = null;

		try {
			DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();

			status = mgr.getTransaction(transactionDefinition);

			if (!dao.checkIfProductsAvailable(order)) {
				throw new OrderWarhouseException("now products!");
			}

			int totalPrice = dao.calculateProductsTotalPrice(order);

			dao.logLocationAndTotalPrice(customer, totalPrice);

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
		dao = productDao;
	}

}
