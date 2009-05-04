package com.pferrot.sharedcalendar.movie.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.security.SecurityUtils;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.movie.MovieConsts;

@ViewController(viewIds={"/auth/movie/myMoviesList.jspx"})
public class MyMoviesListController extends AbstractMoviesListController {
	
	private final static Log log = LogFactory.getLog(MyMoviesListController.class);
	
	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("myMoviesList", "/auth/movie/myMoviesList.iface");
	}

	@Override
	public List<Movie> getMoviesListInternal() {
		return getMovieService().findMoviesOwnedByUsername(SecurityUtils.getCurrentUsername(), getFirstResultIndex(), MovieConsts.NB_MOVIES_PER_PAGE + 1);
	}
}
