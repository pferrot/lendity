package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.model.movie.MovieInstance;
import com.pferrot.sharedcalendar.movie.MovieConsts;
import com.pferrot.sharedcalendar.movie.MovieService;
import com.pferrot.sharedcalendar.movie.MovieUtils;

public abstract class AbstractMoviesListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractMoviesListController.class);
	
	private MovieService movieService;
	
	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
	
	public MovieService getMovieService() {
		return movieService;
	}

	@Override
	public int getNbEntriesPerPage() {
		return MovieConsts.NB_MOVIES_PER_PAGE;
	}
	
	public Integer getNbInstances() {
		final Movie movie = (Movie)getTable().getRowData();
		return movie.getMovieInstances().size();
	}
	
	public int getNbAvailableInstances() {
		final Movie movie = (Movie)getTable().getRowData();
		int counter = 0;
		for (MovieInstance mi: movie.getMovieInstances()) {
			if (!mi.isBorrowed() && !mi.isLocked()) {
				counter++;
			}
		}
		return counter;
	}
	
	public String getMovieOverviewHref() {
		final Movie movie = (Movie)getTable().getRowData();		
		return MovieUtils.getMovieOverviewPageUrl(movie.getId().toString());
	}	
}
