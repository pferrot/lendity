package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.movie.MovieService;
import com.pferrot.sharedcalendar.movie.MovieUtils;
import com.pferrot.sharedcalendar.utils.JsfUtils;

@ViewController(viewIds={"/public/movie/movieOverview.jspx"})
public class MovieOverviewController
{
	private final static Log log = LogFactory.getLog(MovieOverviewController.class);
	
	private MovieService movieService;
	private Long movieId;
	private Movie movie;
	
	@InitView
	public void initView() {
		// Read the movie ID from the request parameter and load the correct movie.
		try {
			final String movieIdString = JsfUtils.getRequestParameter(PagesURL.MOVIE_OVERVIEW_PARAM_MOVIE_ID);
			Movie movie = null;
			if (movieIdString != null) {
				movieId = Long.parseLong(movieIdString);
				movie = movieService.findMovie(movieId);
				setMovie(movie);
			}
			// Movie not found or not movie ID specified.
			if (movie == null) {
				JsfUtils.redirect(PagesURL.MOVIES_LIST);
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.MOVIES_LIST);
		}		
	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
	
	public String getMovieTitle() {
		return getMovie().getTitle();
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public String getMovieEditHref() {		
		return MovieUtils.getMovieEditPageUrl(movie.getId().toString());
	}		
}
