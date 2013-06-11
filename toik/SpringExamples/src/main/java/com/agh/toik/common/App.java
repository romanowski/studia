package com.agh.toik.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.agh.toik.customer.services.CustomerService;

public class App {
	public static void main(String[] args) {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "Spring-Customer.xml" });


        CustomerService custPlain= (CustomerService) appContext
                .getBean("customerService");

        System.out.println("Plain: *************************");
        custPlain.printName();
        System.out.println("Plain: *************************");
        custPlain.printURL();
        System.out.println("Plain: *************************");
        try {
            custPlain.printThrowException();
        } catch (Exception e) {

        }

		CustomerService cust = (CustomerService) appContext
				.getBean("customerServiceProxy");

		System.out.println("Proxy *************************");
		cust.printName();
		System.out.println("Proxy *************************");
		cust.printURL();
		System.out.println("Proxy *************************");
		try {
			cust.printThrowException();
		} catch (Exception e) {

		}

	}
}