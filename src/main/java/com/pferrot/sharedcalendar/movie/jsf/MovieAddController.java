package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.movie.MovieUtils;

public class MovieAddController extends AbstractMovieAddEditController {
	
	private final static Log log = LogFactory.getLog(MovieAddController.class);

	public Long createMovie() {
		Movie movie = new Movie();
		
		movie.setTitle(getTitle());
		movie.setDescription(I18nUtils.getLocalAsString(), getDescription());
		movie.setYear(getYear());
		movie.setDuration(getDuration());
				
		return getMovieService().createMovie(movie);		
	}

	public String getMoviesListHref() {		
		return MovieUtils.getMoviesListUrl();
	}
	
	@Override
	public Long processMovie() {
		return createMovie();
	}
}
