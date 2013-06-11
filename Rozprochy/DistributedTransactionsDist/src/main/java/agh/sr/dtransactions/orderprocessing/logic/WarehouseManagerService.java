package agh.sr.dtransactions.orderprocessing.logic;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;

public interface WarehouseManagerService {

	public int prepareProductsForShipment(Order order, Customer customer)
			throws OrderWarhouseException;

	public void setProductDao(ProductDao productDao);

}
