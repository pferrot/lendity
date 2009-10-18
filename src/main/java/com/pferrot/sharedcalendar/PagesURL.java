package com.pferrot.sharedcalendar;

public interface PagesURL {

	/////////////////////////////////////////////////////////////////////////
	// MOVIE - start
	
	String MOVIE_OVERVIEW = "/public/movie/movieOverview.faces";
	String MOVIE_OVERVIEW_PARAM_MOVIE_ID = "movieID";
	
	String MOVIE_ADD = "/auth/movie/movieAdd.faces";
	
	String MOVIE_EDIT = "/auth/movie/movieEdit.faces";
	String MOVIE_EDIT_PARAM_MOVIE_ID = MOVIE_OVERVIEW_PARAM_MOVIE_ID;
	
	String MOVIE_INSTANCE_OVERVIEW = "/public/movie/movieInstanceOverview.faces";
	String MOVIE_INSTANCE_ADD = "/auth/movie/movieInstanceAdd.faces";
	String MOVIE_INSTANCE_EDIT = "/auth/movie/movieInstanceEdit.faces";
	
	String MOVIES_LIST = "/public/movie/moviesList.faces";
	String MY_MOVIES_LIST = "/auth/movie/myMoviesList.faces";
	
	// MOVIE - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// REGISTRATION - start
	
	String REGISTRATION = "/public/registration/registration.faces";
	
	// REGISTRATION - end
	/////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////
	// PERSON - start	
	
	String PERSONS_LIST = "/auth/person/personsList.faces";
	
	String PERSON_OVERVIEW = "/auth/person/personOverview.faces";
	String PERSON_OVERVIEW_PARAM_PERSON_ID = "personID";
	
	String PERSON_EDIT = "/auth/person/personEdit.faces";
	String PERSON_EDIT_PARAM_PERSON_ID = PERSON_OVERVIEW_PARAM_PERSON_ID;	
	
	// PERSON - end
	/////////////////////////////////////////////////////////////////////////	
}
