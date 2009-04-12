package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.movie.MovieService;
import com.pferrot.sharedcalendar.utils.JsfUtils;

@ViewController(viewIds={"/public/movie/movieOverview.jspx"})
public class MovieOverviewViewController
	//implements org.apache.myfaces.orchestra.viewController.ViewController
{
	private final static Log log = LogFactory.getLog(MovieOverviewViewController.class);
	public static String MOVIE_ID_PARAMETER_NAME = "movieId";
	
	private MovieService movieService;
	private Long movieId;
	
	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("movieOverview", "/public/movie/movieOverview.iface");
		final String movieIdString = JsfUtils.getRequestParameter(MOVIE_ID_PARAMETER_NAME);
		if (movieIdString == null) {
			throw new RuntimeException("No 'movieId' parameter specified.");
		}
		movieId = Long.parseLong(movieIdString);		
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
	
	public String getMovieTitle() {
		return I18nUtils.getLocalizedText(movieService.findMovie(movieId).getTitles());
	}
}
