package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.movie.MovieService;

public class MovieInstanceOverviewController {
	private final static Log log = LogFactory.getLog(MovieInstanceOverviewController.class);
	
	private MovieService movieService;

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
}
