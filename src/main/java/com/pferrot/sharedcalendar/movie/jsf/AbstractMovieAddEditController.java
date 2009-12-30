package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.movie.MovieService;
import com.pferrot.sharedcalendar.utils.JsfUtils;

public abstract class AbstractMovieAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractMovieAddEditController.class);
	
	private MovieService movieService;
	
	private String title;
	private String description;
	private Integer year;
	private Integer duration;
	
	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}	
	
	public MovieService getMovieService() {
		return movieService;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	public abstract Long processMovie();
	
	public String submit() {
		Long movieId = processMovie();
		
		JsfUtils.redirect(PagesURL.MOVIE_OVERVIEW, PagesURL.MOVIE_OVERVIEW_PARAM_MOVIE_ID, movieId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}	
}
