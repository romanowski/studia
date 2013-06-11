package agh.sr.dtransactions.orderprocessing.logic;

import agh.sr.dtransactions.orderprocessing.dao.InvoiceDao;

public interface ProcessOrderService {
	public void processOrder(Order order) throws OrderProcessingException;

	public void setInvoiceDao(InvoiceDao invoiceDao);
}
