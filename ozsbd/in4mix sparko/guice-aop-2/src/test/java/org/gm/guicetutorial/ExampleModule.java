package org.gm.guicetutorial;

import static com.google.inject.matcher.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.gm.guicetutorial.interceptors.RoleValidationInterceptor;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ExampleModule extends AbstractModule {

	private List<Object> injectees = new ArrayList<Object>();

	public void configure() {

		bind(UserManager.class).to(UserManagerImpl.class).in(Scopes.SINGLETON);

		RoleValidationInterceptor roleValidationInterceptor = new RoleValidationInterceptor();

		bindInterceptor(any(), // Match classes.
				annotatedWith(RequiresRole.class), // Match methods.
				roleValidationInterceptor // The interceptor.
		);

		injectees.add(roleValidationInterceptor);
	}

	public List<Object> getInjectees() {

		return injectees;
	}

}
