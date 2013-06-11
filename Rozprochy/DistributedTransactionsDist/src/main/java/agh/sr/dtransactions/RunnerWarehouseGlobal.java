package agh.sr.dtransactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import agh.sr.dtransactions.orderprocessing.dao.ProductDao;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderItem;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService;
import agh.sr.dtransactions.test.ProductDao_ExOnDecrease;
import agh.sr.dtransactions.test.ProductDao_ExOnLogging;

public class RunnerWarehouseGlobal {

	private WarehouseManagerService warehouseService;

	private DataSource productDS;

	private UserTransaction userTransaction;

	private TransactionManager transactionManager;

	private void printBreakLine() {
		System.out
				.println("-----------------------------------------------------------------------");
	}

	public RunnerWarehouseGlobal() throws Exception {
		productDS = DBTxConfiguration
				.createXaDataSource(DBTxConfiguration.PRODUCT_DB_NAME);
		userTransaction = DBTxConfiguration.getUserTransaction();
		transactionManager = DBTxConfiguration.getTransactionManager();
	}

	public void configureGlobalTx() throws Throwable {
//		warehouseService = new WarehouseManagerImpl_GlobalTx(new ProductDao(
//				productDS), userTransaction);
	}

	public void configureGlobalMultipleTx() throws Throwable {
//		warehouseService = new WarehouseManagerImpl_GlobalMultipleTx(
//				new ProductDao(productDS), userTransaction, transactionManager);
	}

	private Customer getCustomer() {
		return new Customer(6, "Jaroslaw", "Wolski", "Ruczaj", 100000);
	}

	private Order getOrder() {
		List<OrderItem> orderItems = Arrays.asList(new OrderItem(1, 3),
				new OrderItem(2, 4), new OrderItem(3, 5));
		return new Order("OrderID", 1, orderItems);
	}

	public void testCommit() throws Throwable {
		Utils.printProductDB(productDS);
		printBreakLine();
		try {
			warehouseService.setProductDao(new ProductDao(productDS));
			warehouseService.prepareProductsForShipment(getOrder(),
					getCustomer());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.printProductDB(productDS);
	}

	public void testExOnDecrease() throws Throwable {
		Utils.printProductDB(productDS);
		printBreakLine();
		try {
			warehouseService.setProductDao(new ProductDao_ExOnDecrease(
					productDS));
			warehouseService.prepareProductsForShipment(getOrder(),
					getCustomer());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.printProductDB(productDS);
	}

	public void testExOnLogging() throws Throwable {
		Utils.printProductDB(productDS);
		printBreakLine();
		try {
			warehouseService
					.setProductDao(new ProductDao_ExOnLogging(productDS));
			warehouseService.prepareProductsForShipment(getOrder(),
					getCustomer());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.printProductDB(productDS);
	}

	public class GlobalTx {

		public void makeCommit() throws Throwable {
			configureGlobalTx();
			testCommit();
		}

		public void makeExOnDecrease() throws Throwable {
			configureGlobalTx();
			testExOnDecrease();
		}

		public void makeExOnLogging() throws Throwable {
			configureGlobalTx();
			testExOnLogging();
		}
	}

	public class GlobalMultipleTx {

		public void makeCommit() throws Throwable {
			configureGlobalMultipleTx();
			testCommit();
		}

		public void makeExOnDecrease() throws Throwable {
			configureGlobalMultipleTx();
			testExOnDecrease();
		}

		public void makeExOnLogging() throws Throwable {
			configureGlobalMultipleTx();
			testExOnLogging();
		}
	}

	public void test() throws Throwable {
		// new GlobalTx().makeCommit(); /* ok */
		// new GlobalTx().makeExOnDecrease(); /* will rollback all */
		// new GlobalTx().makeExOnLogging(); /* will rollback all */

		// new GlobalMultipleTx().makeCommit(); /* ok */
		// new GlobalMultipleTx().makeExOnDecrease(); /* will rollback all
		// beside logging*/
		// new GlobalMultipleTx().makeExOnLogging(); /* will rollback only audit
		// */
	}

	public void runSimultaneousThreads(int workerThreadsAmount,
			final int interationsInThread) throws Throwable {
		Runnable doTransactionTask = new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < interationsInThread; i++) {
						warehouseService.prepareProductsForShipment(getOrder(),
								getCustomer());
					}
				} catch (Exception e) {
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

	public void measure(int workerThreadsAmount, int interationsInThread)
			throws Throwable {
		configureGlobalMultipleTx();
		warehouseService.prepareProductsForShipment(getOrder(), getCustomer());
		long start = System.currentTimeMillis();
		runSimultaneousThreads(workerThreadsAmount, interationsInThread);
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);
	}

	public static void main(String[] args) throws Throwable {
		RunnerWarehouseGlobal runner = new RunnerWarehouseGlobal();
		//runner.test();
		runner.measure(10, 1000);
	}
}
