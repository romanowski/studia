package agh.sr.dtransactions.orderprocessing.logic.impl;

import agh.sr.dtransactions.orderprocessing.dao.CustomerDao;
import agh.sr.dtransactions.orderprocessing.dao.InvoiceDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderProcessingException;
import agh.sr.dtransactions.orderprocessing.logic.ProcessOrderService;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService;

public class ProcessOrderImpl_NonTx implements ProcessOrderService {
	private WarehouseManagerService warehouseService;

	private CustomerDao customerDao;

	private InvoiceDao invoiceDao;

	public ProcessOrderImpl_NonTx(WarehouseManagerService warehouseService,
			CustomerDao customerDao, InvoiceDao invoiceDao) {
		this.warehouseService = warehouseService;
		this.customerDao = customerDao;
		this.invoiceDao = invoiceDao;
	}

	@Override
	public void processOrder(Order order) throws OrderProcessingException {
		try {
			Customer customer = customerDao.getCustomerDetails(order);
			int totalPrice = warehouseService.prepareProductsForShipment(order,
					customer);
			int customerBalance = customer.getBalance();
			customerBalance -= totalPrice;
			customer.setBalance(customerBalance);
			invoiceDao.createInvoiceForCustomer(customer, totalPrice, order);
			customerDao.reduceCustomerBalance(customer, order);
		} catch (Exception e) {
			throw new OrderProcessingException(e);
		}

	}

	@Override
	public void setInvoiceDao(InvoiceDao invoiceDao) {
		this.invoiceDao = invoiceDao;
	}

}
