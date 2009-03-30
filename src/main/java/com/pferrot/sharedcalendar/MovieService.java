package com.pferrot.sharedcalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.MovieDao;

public class MovieService {

	private final static Log log = LogFactory.getLog(MovieService.class);
	
	private MovieDao movieDao;

	public void setMovieDao(MovieDao movieDao) {
		this.movieDao = movieDao;
	}	
}
