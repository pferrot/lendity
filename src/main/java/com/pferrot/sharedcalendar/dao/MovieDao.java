package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.model.movie.MovieInstance;

public interface MovieDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Movie
	Long createMovie(Movie movie);
	
	Movie findMovie(Long movieId);
	
	// Return all movies containing 
	List<Movie> findMoviesByTitle(String title);
	
	void updateMovie(Movie movie);
	
	void deleteMovie(Movie movie);
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// MovieInstance
	Long createMovieInstance(MovieInstance movieInstance);
	
	MovieInstance findMovieInstance(Long movieInstanceId);
	
	void updateMovieInstance(MovieInstance movieInstance);
	
	void deleteMovieInstance(Movie movieInstance);	
}
