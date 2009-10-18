package com.pferrot.sharedcalendar.movie.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.movie.MovieConsts;

public class MoviesListController extends AbstractMoviesListController {
	
	private final static Log log = LogFactory.getLog(MoviesListController.class);
	
	@Override
	public List getListInternal() {		
		// Is there a search string specified?
		if (getSearchString() != null  && getSearchString().trim().length() > 0) {
			// + 1 so that we can know whether there is a next page or not.
			return getMovieService().findMoviesByTitle(getSearchString(), getFirstResultIndex(), MovieConsts.NB_MOVIES_PER_PAGE + 1);
		}
		else {
			// + 1 so that we can know whether there is a next page or not.
			return getMovieService().findMovies(getFirstResultIndex(), MovieConsts.NB_MOVIES_PER_PAGE + 1);
		}
	}
}
