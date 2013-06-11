package org.gm.guicetutorial;

public class UserManagerImpl implements UserManager {

	private User currentUser;

	@Override
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	@Override
	public User getCurrentUser() {
		return currentUser;
	}

}
