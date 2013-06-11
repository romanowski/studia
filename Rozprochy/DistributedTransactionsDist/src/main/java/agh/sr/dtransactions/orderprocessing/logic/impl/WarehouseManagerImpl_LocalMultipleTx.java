package agh.sr.dtransactions.orderprocessing.logic.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.connpassing.ProductDao_ConnPassing;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderWarhouseException;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService_ConnPassing;

public class WarehouseManagerImpl_LocalMultipleTx implements
		WarehouseManagerService_ConnPassing {

	DataSource source;
	ProductDao_ConnPassing dao;

	public WarehouseManagerImpl_LocalMultipleTx(DataSource productDS,
			ProductDao_ConnPassing productDao_ConnPassing) {
		source = productDS;
		dao = productDao_ConnPassing;
	}

	@Override
	public int prepareProductsForShipment(Order order, Customer customer)
			throws OrderWarhouseException {
		Connection conn = null;
		try{
			
			conn = source.getConnection();
			conn.setAutoCommit(false);
			
			if(!dao.checkIfProductsAvailable(order, conn)){
				throw new OrderWarhouseException("now products!");
			}
			
			int totalPrice = dao.calculateProductsTotalPrice(order, conn);
			
			Savepoint beforeLoggin = conn.setSavepoint();
				
			logLocationAndTotalPrice(customer, totalPrice);
			
			if(totalPrice > customer.getBalance()){
				throw new OrderWarhouseException("no chash!");
			}
			
			dao.decreaseProductsAmounts(order, conn);
			conn.commit();
			return totalPrice;
		}
		catch (Exception e) {
			if(conn != null){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			throw new OrderWarhouseException(e);
		}
		finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setProductDao(ProductDao_ConnPassing productDao) {
		this.dao = productDao;
	}

	private void logLocationAndTotalPrice(Customer c, int totalPrice) {

		Connection conn = null;
		try {
			conn = source.getConnection();
			conn.setAutoCommit(false);

			dao.logLocationAndTotalPrice(c, totalPrice, conn);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
