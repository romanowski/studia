package agh.sr.dtransactions.orderprocessing.logic;

import agh.sr.dtransactions.orderprocessing.dao.connpassing.ProductDao_ConnPassing;

public interface WarehouseManagerService_ConnPassing {

	public int prepareProductsForShipment(Order order, Customer customer)
			throws OrderWarhouseException;
	
	public void setProductDao(ProductDao_ConnPassing productDao);

}
