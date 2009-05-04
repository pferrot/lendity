package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.movie.MovieService;
import com.pferrot.sharedcalendar.utils.JsfUtils;

@ViewController(viewIds={"/auth/movie/movieInstanceOverview.jspx"})
public class MovieInstanceOverviewController
	//implements org.apache.myfaces.orchestra.viewController.ViewController
{
	private final static Log log = LogFactory.getLog(MovieInstanceOverviewController.class);
	
	private MovieService movieService;
	
	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("movieInstanceOverview", "/auth/movie/movieInstanceOverview.iface");
	}

//	@PreProcess
//	public void preProcess() {		
//	}
//
//	@PreRenderView
//	public void preRenderView() {		
//	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
}
