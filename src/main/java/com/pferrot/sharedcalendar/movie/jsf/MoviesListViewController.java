package com.pferrot.sharedcalendar.movie.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.movie.MovieService;

@ViewController(viewIds={"/public/movie/moviesList.jspx"})
public class MoviesListViewController
	//implements org.apache.myfaces.orchestra.viewController.ViewController
{
	private final static Log log = LogFactory.getLog(MoviesListViewController.class);
	
	private MovieService movieService;
	private List<Movie> moviesList;
	private HtmlDataTable moviesTable;

	
	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("moviesList", "/public/movie/moviesList.iface");
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
	
	public void setMoviesTable(HtmlDataTable moviesTable) {
		this.moviesTable = moviesTable;
	}

	public HtmlDataTable getMoviesTable() {
		return moviesTable;
	}

	public List<Movie> getMoviesList() {
		// Do not hit the DB everytime this method is called...
		if (moviesList == null) {
			moviesList = movieService.findAllMovies();
		}
		return moviesList;
	}
	
	// Get the movie title in the correct language.
	public String getMovieTitle() {
		final Movie movie = (Movie)moviesTable.getRowData();
		return I18nUtils.getLocalizedText(movie.getTitles());		
	}
	
	
}
