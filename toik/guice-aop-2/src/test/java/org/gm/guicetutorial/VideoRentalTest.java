package org.gm.guicetutorial;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class VideoRentalTest {

	@Inject
	private VideoRental videoRental;

	@Inject
	private UserManager userManager;

	private static User customer;

	private static User employee;

	@BeforeClass
	@SuppressWarnings("serial")
	public static void setupUsers() {

		customer = new User("Peter", new HashSet<String>() {

			{
				add("customer");
			}
		});
		employee = new User("Bob", new HashSet<String>() {

			{
				add("employee");
			}
		});
	}

	@Before
	public void setup() {

		ExampleModule module = new ExampleModule();

		Injector injector = Guice.createInjector(module);
		for (Object oneInjectee : module.getInjectees()) {
			injector.injectMembers(oneInjectee);
		}

		injector.injectMembers(this);
	}

	@Test
	public void testRentMovieSuccessfully() throws Exception {

		userManager.setCurrentUser(customer);
		assertTrue(videoRental.rentMovie(1));
	}

	@Test(expected = IllegalStateException.class)
	public void testRentMovieFailing() throws Exception {

		userManager.setCurrentUser(employee);
		videoRental.rentMovie(1);
	}

	@Test
	public void testRegisterNewMovieSuccessfully() throws Exception {

		userManager.setCurrentUser(employee);
		assertTrue(videoRental.registerNewMovie("The Fugitive"));
	}

	@Test(expected = IllegalStateException.class)
	public void testRegisterNewMovieFailing() throws Exception {

		userManager.setCurrentUser(customer);
		assertTrue(videoRental.registerNewMovie("The Fugitive"));
	}
}
