package com.pferrot.sharedcalendar.movie;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.MovieDao;
import com.pferrot.sharedcalendar.model.Language;
import com.pferrot.sharedcalendar.model.movie.Movie;

public class MovieService {

	private final static Log log = LogFactory.getLog(MovieService.class);
	
	private MovieDao movieDao;
	private ListValueDao listValueDao;

	public void setMovieDao(MovieDao movieDao) {
		this.movieDao = movieDao;
	}
	
	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}
	
	public Language findLanguage(final String languageLabelCode) {
		return (Language)listValueDao.findListValue(languageLabelCode);
	}

	public Movie findMovie(final Long movieId) {
		return movieDao.findMovie(movieId);
	}
	
	public List<Movie> findAllMovies() {
		return movieDao.findAllMovies();
	}
	
	public Long createMovie(final Movie movie) {
		return movieDao.createMovie(movie);
	}
	
}
