package agh.sr.dtransactions.orderprocessing.logic.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.connpassing.ProductDao_ConnPassing;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderWarhouseException;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService_ConnPassing;

public class WarehouseManagerImpl_NonTx implements WarehouseManagerService_ConnPassing {

	private DataSource productDS;

	private ProductDao_ConnPassing productDao;

	public WarehouseManagerImpl_NonTx(DataSource productDS,
			ProductDao_ConnPassing productDao) {
		this.productDS = productDS;
		this.productDao = productDao;
	}

	public int prepareProductsForShipment(Order order, Customer customer)
			throws OrderWarhouseException {
		Connection conn = null;
		try {
			conn = productDS.getConnection();

			if (!productDao.checkIfProductsAvailable(order, conn)) {
				throw new OrderWarhouseException(
						"Not all ordered products are available");
			}
			int totalPrice = productDao
					.calculateProductsTotalPrice(order, conn);
			productDao.logLocationAndTotalPrice(customer, totalPrice, conn);
			if (totalPrice > customer.getBalance()) {
				throw new OrderWarhouseException(
						"Customer does not have enough money");
			}
			productDao.decreaseProductsAmounts(order, conn);

			conn.close();
			return totalPrice;
		} catch (SQLException e) {
			throw new OrderWarhouseException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setProductDao(ProductDao_ConnPassing productDao) {
		this.productDao = productDao;
	}
}
