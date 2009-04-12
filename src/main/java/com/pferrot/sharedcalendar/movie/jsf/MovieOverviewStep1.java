package com.pferrot.sharedcalendar.movie.jsf;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.icesoft.faces.async.render.IntervalRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.pferrot.sharedcalendar.movie.MovieService;
import com.pferrot.sharedcalendar.movie.jsf.MovieOverviewViewController;

public class MovieOverviewStep1
// Renderable is NOT necessary in sync mode.
//implements Renderable, DisposableBean 
{
	
	private final static Log log = LogFactory.getLog(MovieOverviewStep1.class);
	
	private MovieOverviewViewController movieOverviewViewController;
//	private MovieService movieService;
	
	public MovieOverviewStep1() {
		super();
//		state = PersistentFacesState.getInstance();
	}

	public MovieOverviewViewController getMovieOverviewViewController() {
		return movieOverviewViewController;
	}

	public void setMovieOverviewViewController(
			MovieOverviewViewController movieOverviewViewController) {
		this.movieOverviewViewController = movieOverviewViewController;
	}

//	public MovieService getMovieService() {
//		return movieService;
//	}
//
//	public void setMovieService(MovieService movieService) {
//		this.movieService = movieService;
//	}
}
