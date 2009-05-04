package com.pferrot.sharedcalendar.movie;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.utils.JsfUtils;

public class MovieUtils {

	/**
	 * Returns the HTML link to a movie overview page.
	 * 
	 * @param pMovieId
	 * @return
	 */
	public static String getMovieOverviewPageUrl(final String pMovieId) {
		CoreUtils.assertNotNullParameter(pMovieId, "pMovieId");
		
		return JsfUtils.getFullUrl(PagesURL.MOVIE_OVERVIEW, PagesURL.MOVIE_OVERVIEW_PARAM_MOVIE_ID, pMovieId);
	}
	
	/**
	 * 
	 * @param pMovieId
	 * @return
	 */
	public static String getMovieEditPageUrl(final String pMovieId) {
		CoreUtils.assertNotNullParameter(pMovieId, "pMovieId");
		
		return JsfUtils.getFullUrl(PagesURL.MOVIE_EDIT, PagesURL.MOVIE_EDIT_PARAM_MOVIE_ID, pMovieId);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getMovieAddPageUrl() {		
		return JsfUtils.getFullUrl(PagesURL.MOVIE_ADD);
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getMoviesListUrl() {		
		return JsfUtils.getFullUrl(PagesURL.MOVIES_LIST);
	}
}
