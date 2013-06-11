package org.gm.guicetutorial;

public class VideoRental {

	public boolean rentMovie(long movieId) {

		System.out.println(String.format("Movie %s rented.", movieId));

		return true;
	}

	public boolean registerNewMovie(String name) {

		System.out.println(String.format("New movie \"%s\" registered.", name));

		return true;
	}
}
