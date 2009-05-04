package com.pferrot.sharedcalendar.movie.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.movie.MovieConsts;

public class MoviesListController extends AbstractMoviesListController {
	
	private final static Log log = LogFactory.getLog(MoviesListController.class);

	private String movieSearchString = null;
	
	@Override
	public List<Movie> getMoviesListInternal() {		
		// Is there a search string specified?
		if (getMovieSearchString() != null  && getMovieSearchString().trim().length() > 0) {
			// + 1 so that we can know whether there is a next page or not.
			return getMovieService().findMoviesByTitle(getMovieSearchString(), getFirstResultIndex(), MovieConsts.NB_MOVIES_PER_PAGE + 1);
		}
		else {
			// + 1 so that we can know whether there is a next page or not.
			return getMovieService().findMovies(getFirstResultIndex(), MovieConsts.NB_MOVIES_PER_PAGE + 1);
		}
	}	

	public String getMovieSearchString() {
		return movieSearchString;
	}

	public void setMovieSearchString(String movieSearchString) {
		this.movieSearchString = movieSearchString;
		// If the search string changes, go the the first page.
		setFirstResultIndex(0);
	}	

	public String searchByTitle() {
		return "searchByTitle";
	}
	
	public String clearSearchByTitle() {
		setMovieSearchString(null);
		return "clearSearchByTitle";
	}	
}
