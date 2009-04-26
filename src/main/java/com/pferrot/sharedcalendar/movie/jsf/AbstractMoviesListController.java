package com.pferrot.sharedcalendar.movie.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.model.movie.MovieInstance;
import com.pferrot.sharedcalendar.movie.MovieConsts;
import com.pferrot.sharedcalendar.movie.MovieService;

public abstract class AbstractMoviesListController {
	private final static Log log = LogFactory.getLog(AbstractMoviesListController.class);
	
	private List<Movie> moviesListInternal;
	private MovieService movieService;
	private HtmlDataTable moviesTable;
	private int firstResultIndex = 0;
	
	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
	
	public MovieService getMovieService() {
		return movieService;
	}	
	
	public abstract List<Movie> getMoviesListInternal();

	public List<Movie> getMoviesList() {
		// Keep a member variable so that the DB is not accessed everytime.
		moviesListInternal = getMoviesListInternal();
		if (isNextPage()) {
			return moviesListInternal.subList(0, MovieConsts.NB_MOVIES_PER_PAGE);
		}
		else {
			return moviesListInternal;
		}		
	}

	public int getFirstResultIndex() {
		return firstResultIndex;
	}

	public void setFirstResultIndex(int firstResultIndex) {
		this.firstResultIndex = firstResultIndex;
	}

	public void setMoviesTable(HtmlDataTable moviesTable) {
		this.moviesTable = moviesTable;
	}

	public HtmlDataTable getMoviesTable() {
		return moviesTable;
	}	
	
	public Integer getNbInstances() {
		final Movie movie = (Movie)moviesTable.getRowData();
		return movie.getMovieInstances().size();
	}
	
	public int getNbAvailableInstances() {
		final Movie movie = (Movie)moviesTable.getRowData();
		int counter = 0;
		for (MovieInstance mi: movie.getMovieInstances()) {
			if (!mi.isBorrowed() && !mi.isLocked()) {
				counter++;
			}
		}
		return counter;
	}
	
	public boolean isNextPage() {
		return moviesListInternal.size() > MovieConsts.NB_MOVIES_PER_PAGE;
	}
	
	public boolean isPreviousPage() {
		return getFirstResultIndex() > 0;
	}	
	
	public String nextPage() {
		if (isNextPage()) {
			setFirstResultIndex(getFirstResultIndex() + MovieConsts.NB_MOVIES_PER_PAGE);
		}
		return "nextPage";
	}
	
	public String previousPage() {
		if (isPreviousPage()) {
			setFirstResultIndex(getFirstResultIndex() - MovieConsts.NB_MOVIES_PER_PAGE);
		}
		return "previousPage";
	}
	
	public String movieOverview() {
		final Movie movie = (Movie)moviesTable.getRowData();
		return "movieOverview";
	}
}
