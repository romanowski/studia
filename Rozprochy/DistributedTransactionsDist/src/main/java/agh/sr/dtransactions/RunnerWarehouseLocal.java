package agh.sr.dtransactions;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import agh.sr.dtransactions.orderprocessing.dao.connpassing.ProductDao_ConnPassing;
import agh.sr.dtransactions.orderprocessing.logic.Customer;
import agh.sr.dtransactions.orderprocessing.logic.Order;
import agh.sr.dtransactions.orderprocessing.logic.OrderItem;
import agh.sr.dtransactions.orderprocessing.logic.WarehouseManagerService_ConnPassing;
import agh.sr.dtransactions.orderprocessing.logic.impl.WarehouseManagerImpl_NonTx;
import agh.sr.dtransactions.test.ProductDao_ConnPassing_ExOnDecrease;
import agh.sr.dtransactions.test.ProductDao_ConnPassing_ExOnLogging;

public class RunnerWarehouseLocal {

	private WarehouseManagerService_ConnPassing warehouseService;

	private DataSource productDS;

	private void printBreakLine() {
		System.out
				.println("-----------------------------------------------------------------------");
	}
	public RunnerWarehouseLocal() throws Exception {
		productDS = DBTxConfiguration
				.createDataSource(DBTxConfiguration.PRODUCT_DB_NAME);
	}

	public void configureNonTx() throws Throwable {
		warehouseService = new WarehouseManagerImpl_NonTx(productDS,
				new ProductDao_ConnPassing());
	}

	public void configureLocalTx() throws Throwable {
//		warehouseService = new WarehouseManagerImpl_LocalTx(productDS,
//				new ProductDao_ConnPassing());
	}

	public void configureLocalSavepointsTx() throws Throwable {
//		warehouseService = new WarehouseManagerImpl_LocalSavepointsTx(
//				productDS, new ProductDao_ConnPassing());
	}
	
	public void configureLocalMultipleTx() throws Throwable {
//		warehouseService = new WarehouseManagerImpl_LocalMultipleTx(
//				productDS, new ProductDao_ConnPassing());
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
			warehouseService.setProductDao(new ProductDao_ConnPassing());
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
			warehouseService
					.setProductDao(new ProductDao_ConnPassing_ExOnDecrease());
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
					.setProductDao(new ProductDao_ConnPassing_ExOnLogging());
			warehouseService.prepareProductsForShipment(getOrder(),
					getCustomer());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Utils.printProductDB(productDS);
	}

	public class NonTx {
		public void makeCommit() throws Throwable {
			configureNonTx();
			testCommit();
		}

		public void makeExOnDecrease() throws Throwable {
			configureNonTx();
			testExOnDecrease();
		}
	}

	public class LocalTx {

		public void makeCommit() throws Throwable {
			configureLocalTx();
			testCommit();
		}

		public void makeExOnDecrease() throws Throwable {
			configureLocalTx();
			testExOnDecrease();
		}
	}

	public class LocalSavepointsTx {

		public void makeCommit() throws Throwable {
			configureLocalSavepointsTx();
			testCommit();
		}

		public void makeExOnDecrease() throws Throwable {
			configureLocalSavepointsTx();
			testExOnDecrease();
		}

		public void makeExOnLogging() throws Throwable {
			configureLocalSavepointsTx();
			testExOnLogging();
		}
	}
	
	public class LocalMultipleTx {

		public void makeCommit() throws Throwable {
			configureLocalMultipleTx();
			testCommit();
		}

		public void makeExOnDecrease() throws Throwable {
			configureLocalMultipleTx();
			testExOnDecrease();
		}

		public void makeExOnLogging() throws Throwable {
			configureLocalMultipleTx();
			testExOnLogging();
		}
	}	

	public void test() throws Throwable {
//		new NonTx().makeCommit(); /* ok */
//		new NonTx().makeExOnDecrease(); /* will write only to audit */
//		
//		new LocalTx().makeCommit(); /* ok */
//		new LocalTx().makeExOnDecrease(); /* will rollback all */
		
//		new LocalSavepointsTx().makeCommit(); /* ok */
//		new LocalSavepointsTx().makeExOnDecrease(); /* will rollback all */
//		new LocalSavepointsTx().makeExOnLogging(); /* will rollback only audit */
		
//		new LocalMultipleTx().makeCommit(); /* ok */
//		new LocalMultipleTx().makeExOnDecrease(); /* will rollback all beside logging */
//		new LocalMultipleTx().makeExOnLogging(); /* will rollback logging */		
	}

	public static void main(String[] args) throws Throwable {
		new RunnerWarehouseLocal().test();
	}
}
