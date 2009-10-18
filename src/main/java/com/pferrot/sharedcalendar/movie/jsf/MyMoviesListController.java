package com.pferrot.sharedcalendar.movie.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.security.SecurityUtils;
import com.pferrot.sharedcalendar.movie.MovieConsts;

public class MyMoviesListController extends AbstractMoviesListController {
	
	private final static Log log = LogFactory.getLog(MyMoviesListController.class);

	@Override
	public List getListInternal() {
		return getMovieService().findMoviesOwnedByUsername(SecurityUtils.getCurrentUsername(), getFirstResultIndex(), MovieConsts.NB_MOVIES_PER_PAGE + 1);
	}
}
