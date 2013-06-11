package agh.sr.dtransactions.orderprocessing.logic;

public class Customer {

	private int id;
	private String firstName;
	private String lastName;
	private String address;
	private int balance;

	public Customer(int id, String firstName, String lastName, String address,
			int balance) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.balance = balance;
	}

	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
}
