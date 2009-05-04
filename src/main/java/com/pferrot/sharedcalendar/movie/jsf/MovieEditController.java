package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.movie.MovieUtils;
import com.pferrot.sharedcalendar.utils.JsfUtils;

@ViewController(viewIds={"/auth/movie/movieEdit.jspx"})
public class MovieEditController extends AbstractMovieAddEditController {
	
	private final static Log log = LogFactory.getLog(MovieEditController.class);
	
	private Movie movie;

	@InitView
	public void initView() {
		// Read the movie ID from the request parameter and load the correct movie.
		try {
			final String movieIdString = JsfUtils.getRequestParameter(PagesURL.MOVIE_EDIT_PARAM_MOVIE_ID);
			if (movieIdString != null) {
				setMovie(getMovieService().findMovie(Long.parseLong(movieIdString)));
			}
			// Movie not found or no movie ID specified.
			if (getMovie() == null) {
				JsfUtils.redirect(PagesURL.MOVIES_LIST);
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.MOVIES_LIST);
		}		
	}

	public Movie getMovie() {
		return movie;
	}
	
	private void setMovie(final Movie pMovie) {
		movie = pMovie;
		
		// Initialize the model to be edited.
		setTitle(pMovie.getTitle());
		setDescription(pMovie.getDescription(I18nUtils.getLocalAsString()));
		setDuration(pMovie.getDuration());
		setYear(pMovie.getYear());		
	}	

	public Long updateMovie() {		
		getMovie().setTitle(getTitle());
		getMovie().setDescription(I18nUtils.getLocalAsString(), getDescription());
		getMovie().setYear(getYear());
		getMovie().setDuration(getDuration());
				
		getMovieService().updateMovie(getMovie());
		
		return getMovie().getId();
	}

	public String getMovieOverviewHref() {		
		return MovieUtils.getMovieOverviewPageUrl(movie.getId().toString());
	}	

	@Override
	public Long processMovie() {
		return updateMovie();
	}	
}
