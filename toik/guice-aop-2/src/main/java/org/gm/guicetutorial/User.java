package org.gm.guicetutorial;

import java.util.Set;

public class User {

	private String name;

	private Set<String> roles;

	public User(String name, Set<String> roles) {

		super();
		this.name = name;
		this.roles = roles;
	}

	public String getName() {

		return name;
	}

	public Set<String> getRoles() {

		return roles;
	}

	@Override
	public String toString() {

		return name;
	}

}