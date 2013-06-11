package org.gm.guicetutorial;

import static com.google.inject.matcher.Matchers.*;

import org.gm.guicetutorial.interceptors.TracingInterceptor;

import com.google.inject.AbstractModule;

public class ExampleModule extends AbstractModule {

	public void configure() {

		bindInterceptor(subclassesOf(VideoRental.class), any(),
				new TracingInterceptor());
	}

}
