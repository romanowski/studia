package edu.romanow.client;

import Ice.Application;
import Laboratory.LabolatoryPrx;
import Laboratory.LabolatoryPrxHelper;

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 22.05.12
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class JavaClient extends Application {

	@Override
	public int run(String[] args) {
		String pass = "alala", login = "alll";
		String ep = communicator().getProperties().getProperty("server.endpoints");

		LabolatoryPrx base = LabolatoryPrxHelper.checkedCast(communicator().stringToProxy("dev: " + ep));

		System.out.println("State:");
		for(String desc:base.describe()){
			System.out.println(desc);
		}
		System.out.println("##################");

		//TODO rest of stuff

		return 0;
	}

	public static void main(String[] args) {
		new JavaClient().main("JavaClient", args, "config.client");
	}
}
