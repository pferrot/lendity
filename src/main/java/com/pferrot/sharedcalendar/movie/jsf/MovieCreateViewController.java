package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.movie.MovieService;

@ViewController(viewIds={"/public/movie/movieCreate.jspx"})
public class MovieCreateViewController
	//implements org.apache.myfaces.orchestra.viewController.ViewController
{
	private final static Log log = LogFactory.getLog(MovieCreateViewController.class);
	
	private MovieService movieService;
	
	private String title;
	private String description;
	private Integer year;
	private Integer duration;
	
	@InitView
	public void initView() {
		// TODO should not be public.
		ConversationUtils.ensureConversationRedirect("movieCreate", "/public/movie/movieCreate.iface");
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
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Long createMovie() {
		Movie movie = new Movie();
		
		movie.setTitle(getTitle());
		movie.setDescription(I18nUtils.getLocalAsString(), getDescription());
		movie.setYear(getYear());
		movie.setDuration(getDuration());
				
		return movieService.createMovie(movie);		
	}
}
