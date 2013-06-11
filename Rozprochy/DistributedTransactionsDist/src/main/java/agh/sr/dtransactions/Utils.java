package agh.sr.dtransactions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class Utils {

	public static void printInvoice(DataSource ds) throws SQLException {
		Connection conn = ds.getConnection();
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		ResultSet r = s
				.executeQuery("SELECT id, customer_first_name, customer_last_name, customer_address, total_price FROM invoice");
		System.out.println("Invoice Table (the last 3 rows):");
		if (r.last()) {
			int rowCount = r.getRow();
			for (int j = 0; j < 3; j++) {
				System.out.println(String.format("Row %2d: %3d %10s %12s %12s %3d",
						rowCount--, r.getInt(1), r.getString(2),
						r.getString(3), r.getString(4), r.getInt(5)));
				if (!r.previous()) {
					break;
				}
			}
		}
		System.out.println("");
		conn.close();
	}

	public static void printCustomerDB(DataSource ds) throws SQLException {
		Connection conn = ds.getConnection();
		Statement s = conn.createStatement();
		ResultSet r = s
				.executeQuery("SELECT id, first_name, last_name, address, balance FROM customer");
		int i = 1;
		System.out.println("Customers Table:");
		while (r.next()) {
			System.out.println(String.format("Row %2d: %3d %10s %12s %3d", i++,
					r.getInt(1), r.getString(2), r.getString(3), r.getInt(5)));
		}
		System.out.println("");
		conn.close();
	}

	public static void printProductDB(DataSource ds) throws SQLException {
		Connection conn = ds.getConnection();
		Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);

		ResultSet r = s
				.executeQuery("SELECT id, name, price, amount FROM product");
		int i = 1;
		System.out.println("Products Table:");
		while (r.next()) {
			System.out.println(String.format("Row %2d: %3d %10s %3d %3d", i++,
					r.getInt(1), r.getString(2), r.getInt(3), r.getInt(4)));
		}
		System.out.println("");

		r = s.executeQuery("SELECT address, total_price FROM product_audit");
		System.out.println("ProductsAudit Table (the last 3 rows):");
		if (r.last()) {
			int rowCount = r.getRow();
			for (int j = 0; j < 3; j++) {
				System.out.println(String.format("Row %2d: %10s %10s",
						rowCount--, r.getString(1), r.getString(2)));
				if (!r.previous()) {
					break;
				}
			}
		}
		System.out.println("");
		conn.close();
	}
}
