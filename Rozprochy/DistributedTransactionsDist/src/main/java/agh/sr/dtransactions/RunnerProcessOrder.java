package agh.sr.dtransactions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.ChainedTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import agh.sr.dtransactions.orderprocessing.dao.CustomerDao;
import agh.sr.dtransactions.orderprocessing.dao.InvoiceDao;
import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.dao.idempotent.InvoiceDao_Idempotent;
import agh.sr.dtransactions.orderprocessing.dao.idempotent.ProductDao_Idempotent;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderItem;
import agh.sr.dtransactions.orderprocessing.logic.OrderProcessingException;
import agh.sr.dtransactions.orderprocessing.logic.ProcessOrderService;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService;
import agh.sr.dtransactions.test.InvoiceDao_ExOnCreate;
import agh.sr.dtransactions.test.ProductDao_ExOnLogging;

public class RunnerProcessOrder {

	private WarehouseManagerService warehouseService;

	private ProcessOrderService processOrderService;

	private DataSource productDS;

	private DataSource customerDS;

	private DataSource invoiceDS;

	private ProductDao productDao;

	private CustomerDao customerDao;

	private InvoiceDao invoiceDao;

	private PlatformTransactionManager globalTransactionManager;

	private PlatformTransactionManager productTransactionManager;

	private PlatformTransactionManager customerTransactionManager;

	private PlatformTransactionManager invoiceTransactionManager;

	private Order getOrder() {
		List<OrderItem> orderItems = Arrays.asList(new OrderItem(1, 3),
				new OrderItem(2, 4), new OrderItem(3, 5));
		return new Order(UUID.randomUUID().toString(), 6, orderItems);
	}

	private void printBreakLine() {
		System.out
				.println("-----------------------------------------------------------------------");
	}

	private void printTables() throws SQLException {
		Utils.printCustomerDB(customerDS);
		Utils.printProductDB(productDS);
		Utils.printInvoice(invoiceDS);
	}

	public void testCommit() throws Throwable {
		printTables();
		printBreakLine();
		try {
			warehouseService.setProductDao(productDao);
			processOrderService.setInvoiceDao(invoiceDao);
			processOrderService.processOrder(getOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		printTables();
	}

	public void testExOnCreateInvoice() throws Throwable {
		printTables();
		printBreakLine();
		try {
			warehouseService.setProductDao(productDao);
			processOrderService.setInvoiceDao(new InvoiceDao_ExOnCreate(
					invoiceDS));
			processOrderService.processOrder(getOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		printTables();
	}

	public void testExOnLogging() throws Throwable {
		printTables();
		printBreakLine();
		try {
			warehouseService
					.setProductDao(new ProductDao_ExOnLogging(productDS));
			processOrderService.setInvoiceDao(invoiceDao);
			processOrderService.processOrder(getOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		printTables();
	}

	class GlobalSpringTx {
		private void configureDao() {
			productDao = new ProductDao(productDS);
			customerDao = new CustomerDao(customerDS);
			invoiceDao = new InvoiceDao(invoiceDS);
		}

		public void configureImpl() throws Throwable {
			configureDao();
//			warehouseService = new WarehouseManagerImpl_SpringMultipleTx(
//					productDao, globalTransactionManager);
//			processOrderService = new ProcessOrderImpl_SpringTx(
//					warehouseService, customerDao, invoiceDao,
//					globalTransactionManager);
		}

		public void makeCommit() throws Throwable {
			configureImpl();
			testCommit();
		}

		public void makeExOnCreateInvoice() throws Throwable {
			configureImpl();
			testExOnCreateInvoice();
		}

		public void makeExOnLogging() throws Throwable {
			configureImpl();
			testExOnLogging();
		}

	}

	class LocalSpringTx {

		private void configureDao() {
			productDao = new ProductDao_Idempotent(productDS);
			customerDao = new CustomerDao(customerDS);
			invoiceDao = new InvoiceDao_Idempotent(invoiceDS);
		}

		public void configureImpl() throws Throwable {
			configureDao();
//			warehouseService = new WarehouseManagerImpl_SpringMultipleTx(
//					productDao, globalTransactionManager);
//			processOrderService = new ProcessOrderImpl_SpringTx(
//					warehouseService, customerDao, invoiceDao,
//					globalTransactionManager);
		}

		public void makeCommit() throws Throwable {
			configureImpl();
			testCommit();
		}

		public void makeExOnCreateInvoice() throws Throwable {
			configureImpl();
			testExOnCreateInvoice();
		}

		public void makeExOnLogging() throws Throwable {
			configureImpl();
			testExOnLogging();
		}

	}

	private void setupGlobalTransactionManager() throws Exception {
		productDS = DBTxConfiguration
				.createXaDataSource(DBTxConfiguration.PRODUCT_DB_NAME);
		customerDS = DBTxConfiguration
				.createXaDataSource(DBTxConfiguration.CUSTOMER_DB_NAME);
		invoiceDS = DBTxConfiguration
				.createXaDataSource(DBTxConfiguration.INVOICE_DB_NAME);
		globalTransactionManager = DBTxConfiguration
				.getSpringTransactionManager_WithJTA();
	}

	private void setupLocalTransactionManager() throws Exception {
		productDS = new TransactionAwareDataSourceProxy(
				DBTxConfiguration
						.createPooledDataSource(DBTxConfiguration.PRODUCT_DB_NAME));
		customerDS = new TransactionAwareDataSourceProxy(
				DBTxConfiguration
						.createPooledDataSource(DBTxConfiguration.CUSTOMER_DB_NAME));
		invoiceDS = new TransactionAwareDataSourceProxy(
				DBTxConfiguration
						.createPooledDataSource(DBTxConfiguration.INVOICE_DB_NAME));
		productTransactionManager = DBTxConfiguration
				.getSpringTransactionManager_WithJDBC(productDS);
		customerTransactionManager = DBTxConfiguration
				.getSpringTransactionManager_WithJDBC(customerDS);
		invoiceTransactionManager = DBTxConfiguration
				.getSpringTransactionManager_WithJDBC(invoiceDS);
		ChainedTransactionManager chainedTM = new ChainedTransactionManager();
		chainedTM.setTransactionManagers(customerTransactionManager,
				productTransactionManager, invoiceTransactionManager);
		globalTransactionManager = chainedTM;
	}

	public void testGlobal() throws Throwable {
		setupGlobalTransactionManager();
		// new GlobalSpringTx().makeCommit(); /* ok */
		// new GlobalSpringTx().makeExOnCreateInvoice(); /* will rollback all beside logging */
		// new GlobalSpringTx().makeExOnLogging(); /* will rollback only audit */
	}

	public void testLocal() throws Throwable {
		setupLocalTransactionManager();
		// new LocalSpringTx().makeCommit(); /* ok */
		// new LocalSpringTx().makeExOnCreateInvoice(); /* will rollback all beside logging */
		// new LocalSpringTx().makeExOnLogging(); /* will rollback only audit */
	}

	public void test() throws Throwable {
		// testGlobal();
		// testLocal();
	}

	public void runSimultaneousThreads(int workerThreadsAmount,
			final int interationsInThread) throws Throwable {
		Runnable doTransactionTask = new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < interationsInThread; i++) {
						processOrderService.processOrder(getOrder());
					}
				} catch (OrderProcessingException e) {
					e.printStackTrace();
				}
			}
		};
		List<Thread> workerThreads = new ArrayList<Thread>();
		for (int i = 0; i < workerThreadsAmount; i++) {
			Thread t = new Thread(doTransactionTask);
			workerThreads.add(t);
			t.start();
		}
		for (Thread t : workerThreads) {
			t.join();
		}
	}

	public void measureGlobal(int workerThreadsAmount, int interationsInThread)
			throws Throwable {
		setupGlobalTransactionManager();
		final GlobalSpringTx manager = new GlobalSpringTx();
		manager.configureImpl();
		long start = System.currentTimeMillis();
		runSimultaneousThreads(workerThreadsAmount, interationsInThread);
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);
	}

	public void measureLocal(int workerThreadsAmount, int interationsInThread)
			throws Throwable {
		setupLocalTransactionManager();
		final GlobalSpringTx manager = new GlobalSpringTx();
		manager.configureImpl();
		long start = System.currentTimeMillis();
		runSimultaneousThreads(workerThreadsAmount, interationsInThread);
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);
	}

	public static void main(String[] args) throws Throwable {
		RunnerProcessOrder r = new RunnerProcessOrder();
		r.test();
		// r.measureGlobal(10, 200);
		// r.measureLocal(10, 200);
	}
}
