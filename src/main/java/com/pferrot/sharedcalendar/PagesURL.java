package com.pferrot.sharedcalendar;

public interface PagesURL {

	/////////////////////////////////////////////////////////////////////////
	// MOVIE - start
	
	String MOVIE_OVERVIEW = "/public/movie/movieOverview.iface";
	String MOVIE_OVERVIEW_PARAM_MOVIE_ID = "movieID";
	
	String MOVIE_ADD = "/auth/movie/movieAdd.iface";
	
	String MOVIE_EDIT = "/auth/movie/movieEdit.iface";
	String MOVIE_EDIT_PARAM_MOVIE_ID = MOVIE_OVERVIEW_PARAM_MOVIE_ID;
	
	String MOVIE_INSTANCE_OVERVIEW = "/public/movie/movieInstanceOverview.iface";
	String MOVIE_INSTANCE_ADD = "/auth/movie/movieInstanceAdd.iface";
	String MOVIE_INSTANCE_EDIT = "/auth/movie/movieInstanceEdit.iface";
	
	String MOVIES_LIST = "/public/movie/moviesList.iface";
	String MY_MOVIES_LIST = "/auth/movie/myMoviesList.iface";
	
	// MOVIE - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// REGISTRATION - start
	
	String REGISTRATION = "/public/registration/registration.iface";
	
	// REGISTRATION - end
	/////////////////////////////////////////////////////////////////////////	
}
