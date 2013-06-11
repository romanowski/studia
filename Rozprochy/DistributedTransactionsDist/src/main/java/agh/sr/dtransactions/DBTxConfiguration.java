package agh.sr.dtransactions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.derby.jdbc.EmbeddedXADataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class DBTxConfiguration {

	public static final String CUSTOMER_DB_NAME = "CUSTOMER_DB";

	public static final String PRODUCT_DB_NAME = "PRODUCT_DB";

	public static final String INVOICE_DB_NAME = "INVOICE_DB";

	private static String DERBY_URL_PREFIX = "jdbc:derby:";

	private static final String CUSTOMER_DB_SCHEMA = "market-customer-db.sql";

	private static final String PRODUCT_DB_SCHEMA = "market-product-db.sql";

	private static final String INVOICE_DB_SCHEMA = "market-invoice-db.sql";

	private static final String SCHEMA_DIR = "src/main/resources/";

	private static final int NUMBER_OF_USERS = 10;

	private static String[][] CUSTOMERS = new String[][] {
			{ "Marek", "Kowalski", "Krakow" }, { "Jacek", "Nowak", "Wroclaw" },
			{ "Maciek", "Zielinski", "Poznan" },
			{ "Wojciech", "Zmuda", "Gdansk" },
			{ "Slawomir", "Zalewski", "Olsztyn" },
			{ "Jaroslaw", "Wolski", "Szczecin" },
			{ "Zenon", "Gawron", "Lublin" },
			{ "Stanislaw", "Zablocki", "Zamosc" },
			{ "Stefan", "Burczymucha", "Kluczbork" },
			{ "Jozef", "Skowronski", "Swinoujscies" } };

	private static final int NUMBER_OF_PRODUCTS = 10;

	private static String[][] PRODUCTS = new String[][] { { "Mydlo", "3" },
			{ "Zapalki", "1" }, { "Kiwi", "7" }, { "Proszek", "10" },
			{ "Piwo", "5" }, { "Cebula", "2" }, { "Truskawki", "6" },
			{ "Maliny", "6" }, { "Cukier", "4" }, { "Sol", "3" } };

	static {
		try {
			ensureThatDBisCreated();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Connection ensureThatDBisCreated(String name,
			String dbSchemaFile) throws Exception {
		String dbUrl = DERBY_URL_PREFIX + name;
		File dbDirectory = new File(name);
		if (!dbDirectory.exists()) {
			Runtime.getRuntime().exec("cmd.exe /c rmdir /q /s " + name);
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection dbConnection = DriverManager.getConnection(dbUrl
					+ ";create=true");
			SqlScriptRunner scriptRunner = new SqlScriptRunner(dbConnection,
					true, true);
			File dbScript = new File(SCHEMA_DIR + dbSchemaFile);
			BufferedReader dbScriptReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(dbScript)));
			scriptRunner.runScript(dbScriptReader);
			return dbConnection;
		}
		return null;
	}

	private static void generateCustomersAccounts(Connection c)
			throws Exception {
		PreparedStatement p = c
				.prepareStatement("INSERT INTO customer( id, first_name, last_name, address, balance) VALUES ( ? , ? , ? , ? , ?)");
		for (int i = 1; i <= NUMBER_OF_USERS; i++) {
			p.setInt(1, i);
			p.setString(2, CUSTOMERS[i - 1][0]);
			p.setString(3, CUSTOMERS[i - 1][1]);
			p.setString(4, CUSTOMERS[i - 1][2]);
			p.setInt(5, 1000000);
			p.execute();
		}
	}

	private static void generateProducts(Connection c) throws Exception {
		PreparedStatement p = c
				.prepareStatement("INSERT INTO product( id, name, price, amount) VALUES ( ? , ? , ? , ? )");
		for (int i = 1; i <= NUMBER_OF_PRODUCTS; i++) {
			p.setInt(1, i);
			p.setString(2, PRODUCTS[i - 1][0]);
			p.setInt(3, Integer.parseInt(PRODUCTS[i - 1][1]));
			p.setInt(4, 100000000);
			p.execute();
		}
	}

	private static void ensureThatDBisCreated() throws Exception {
		Connection c = ensureThatDBisCreated(CUSTOMER_DB_NAME,
				CUSTOMER_DB_SCHEMA);
		if (c != null) {
			generateCustomersAccounts(c);
			c.close();
		}
		c = ensureThatDBisCreated(PRODUCT_DB_NAME, PRODUCT_DB_SCHEMA);
		if (c != null) {
			generateProducts(c);
			c.close();
		}
		c = ensureThatDBisCreated(INVOICE_DB_NAME, INVOICE_DB_SCHEMA);
		if (c != null) {
			c.close();
		}
	}

	public static DataSource createDataSource(String dbName) throws Exception {
		EmbeddedDataSource derbyDataSource = new EmbeddedDataSource();
		derbyDataSource.setDatabaseName(dbName);
		return derbyDataSource;
	}

	public static DataSource createPooledDataSource(String dbName)
			throws Exception {
		BasicDataSource pooledDataSource = new BasicDataSource();
		pooledDataSource
				.setDriverClassName("org.apache.derby.jdbc.EmbeddedDataSource");
		pooledDataSource.setUrl("jdbc:derby:" + dbName);

		pooledDataSource.setMaxActive(100);
		pooledDataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

		return pooledDataSource;
	}

	// Configuration for atomikos:
	//
	// public static DataSource createXaDataSource(String dbName) throws
	// Exception {
	// EmbeddedXADataSource derbyXaDataSource = new EmbeddedXADataSource();
	// derbyXaDataSource.setDatabaseName(dbName);
	//
	// AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
	// xaDataSource.setXaDataSource(derbyXaDataSource);
	// xaDataSource.setUniqueResourceName(dbName);
	// xaDataSource.setMaxPoolSize(100);
	// return xaDataSource;
	// }
	//
	// public static TransactionManager getTransactionManager() throws Exception
	// {
	// //initializeTm();
	// UserTransactionManager userTransactionManager = new
	// UserTransactionManager();
	// userTransactionManager.setForceShutdown(false);
	// userTransactionManager.init();
	// return userTransactionManager;
	// }
	//
	// public static UserTransaction getUserTransaction() throws Exception {
	// //initializeTm();
	// UserTransactionImp userTransactionImp = new UserTransactionImp();
	// userTransactionImp.setTransactionTimeout(120);
	// return userTransactionImp;
	// }
	//

	public static TransactionManager getTransactionManager() throws Exception {
		return TransactionManagerServices.getTransactionManager();
	}

	public static UserTransaction getUserTransaction() throws Exception {
		TransactionManagerServices.getConfiguration().setJournal("null");
		return TransactionManagerServices.getTransactionManager();
	}

	public static DataSource createXaDataSource(String dbName) throws Exception {
		PoolingDataSource poolingDataSource = new PoolingDataSource();
		poolingDataSource.setClassName(EmbeddedXADataSource.class.getName());
		poolingDataSource.setUniqueName(dbName);

		Properties props = new Properties();
		props.setProperty("databaseName", dbName);

		poolingDataSource.setDriverProperties(props);
		poolingDataSource.setMaxPoolSize(100);
		poolingDataSource.setAllowLocalTransactions(true);
		poolingDataSource.init();
		
		poolingDataSource.setIsolationLevel("READ_UNCOMMITTED");

		return poolingDataSource;
	}

	public static PlatformTransactionManager getSpringTransactionManager_WithJDBC(
			DataSource ds) throws Exception {
		DataSourceTransactionManager springTransactionManager = new DataSourceTransactionManager();
		springTransactionManager.setDataSource(ds);
		return springTransactionManager;
	}

	public static PlatformTransactionManager getSpringTransactionManager_WithJTA()
			throws Exception {
		UserTransaction userTransaction = getUserTransaction();
		TransactionManager transactionManager = getTransactionManager();
		return new JtaTransactionManager(userTransaction, transactionManager);
	}

}
