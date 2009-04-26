package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.model.movie.MovieInstance;

public interface MovieDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Movie
	Long createMovie(Movie movie);
	
	Movie findMovie(Long movieId);
	
	// Return all movies containing title.
	List<Movie> findMoviesByTitle(String title);
	List<Movie> findMoviesByTitle(String title, int pFirstResult, int pMaxResults);
	
	List<Movie> findAllMovies();
	
	List<Movie> findMovies(int pFirstResult, int pMaxResults);
	
	List<Movie> findMoviesOwnedByUser(final User pUser, int pFirstResult, int pMaxResults);
	List<Movie> findMoviesOwnedByUser(final String pUsername, int pFirstResult, int pMaxResults);

	List<Movie> findMoviesBorrowedByUser(final User pUser, int pFirstResult, int pMaxResults);
	List<Movie> findMoviesBorrowedByUser(final String pUsername, int pFirstResult, int pMaxResults);
	
	void updateMovie(Movie movie);
	
	void deleteMovie(Movie movie);
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// MovieInstance
	Long createMovieInstance(MovieInstance movieInstance);
	
	MovieInstance findMovieInstance(Long movieInstanceId);
	
	// All movies where owner = user.
	List<MovieInstance> findMovieInstancesOwnedByUser(User user);
	
	// All movies where borrower = user.
	List<MovieInstance> findMovieInstancesBorrowedNowByUser(User user);
	
	// All movies that the user borrowed at some point.
	List<MovieInstance> findMovieInstancesBorrowedAnytimeByUser(User user);
	
	// All movies where the user is in the wait list.
	List<MovieInstance> findMovieInstancesWantedByUser(User user);
	
	void updateMovieInstance(MovieInstance movieInstance);
	
	void deleteMovieInstance(Movie movieInstance);	
}
