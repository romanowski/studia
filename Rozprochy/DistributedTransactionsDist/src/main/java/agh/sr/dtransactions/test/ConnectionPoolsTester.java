package agh.sr.dtransactions.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import agh.sr.dtransactions.DBTxConfiguration;

public class ConnectionPoolsTester {

	public static void testDataSource(DataSource ds) throws SQLException {
		List<Connection> openedConnections = new ArrayList<Connection>();
		for (int i = 0; i < 10000; i++) {
			for (int j = 0; j < 10; j++) {
				openedConnections.add(ds.getConnection());
			}
			for (int j = 0; j < 10; j++) {
				openedConnections.remove(0).close();
			}
		}
	}

	public static void testRegularDataSource(DataSource ds) throws Exception {
	}

	public static void testRegularDataSource() throws Exception {
		DataSource ds = DBTxConfiguration
				.createDataSource(DBTxConfiguration.PRODUCT_DB_NAME);
		testDataSource(ds);
	}

	public static void testPooledDataSource() throws Exception {
		DataSource ds = DBTxConfiguration
				.createPooledDataSource(DBTxConfiguration.PRODUCT_DB_NAME);
		testDataSource(ds);
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		// testRegularDataSource();
		testPooledDataSource();
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);
	}

}
