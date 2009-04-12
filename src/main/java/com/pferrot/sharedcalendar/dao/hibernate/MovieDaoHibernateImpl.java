package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.dao.MovieDao;
import com.pferrot.sharedcalendar.model.movie.Movie;
import com.pferrot.sharedcalendar.model.movie.MovieInstance;

public class MovieDaoHibernateImpl extends HibernateDaoSupport implements MovieDao {

	/////////////////////////////////////////////////////////////////////////////////////////
	// Movie
	public Long createMovie(final Movie movie) {
		return (Long)getHibernateTemplate().save(movie);
	}

	public void deleteMovie(final Movie movie) {
		getHibernateTemplate().delete(movie);
	}

	public void updateMovie(final Movie movie) {
		getHibernateTemplate().update(movie);
	}

	public Movie findMovie(final Long movieId) {
		return (Movie)getHibernateTemplate().load(Movie.class, movieId);
	}

	public List<Movie> findMoviesByTitle(final String pTitle) {
		CoreUtils.assertNotEmptyStringParameter(pTitle, "pTitle");
		final String titleLower = pTitle.trim().toLowerCase();
		return getHibernateTemplate().find("from Movie m where lower(m.title) like '%" + titleLower + "%'");
	}
	
	public List<Movie> findAllMovies() {
		return getHibernateTemplate().find("from Movie m");
	}
	
	// TODO
	public List<Movie> findAllMoviesOrderedByTitle(final String pLanguage) {
		CoreUtils.assertNotEmptyStringParameter(pLanguage, "pLanguage");
		
		return getHibernateTemplate().find("select m from Movie m inner join m.titles titles " +
				"where titles.borrower = ?", pUser);
	}	

	/////////////////////////////////////////////////////////////////////////////////////////
	// MovieInstance
	public Long createMovieInstance(final MovieInstance movieInstance) {
		return (Long)getHibernateTemplate().save(movieInstance);
	}

	public void deleteMovieInstance(final Movie movieInstance) {
		getHibernateTemplate().delete(movieInstance);		
	}

	public void updateMovieInstance(final MovieInstance movieInstance) {
		getHibernateTemplate().update(movieInstance);		
	}
	
	public MovieInstance findMovieInstance(final Long movieInstanceId) {
		return (MovieInstance)getHibernateTemplate().load(MovieInstance.class, movieInstanceId);
	}

	public List<MovieInstance> findMovieInstancesBorrowedAnytimeByUser(final User pUser) {
		CoreUtils.assertNotNullParameter(pUser, "pUser");
		return getHibernateTemplate().find("select mi from MovieInstance mi inner join mi.borrowerHistoryEntries bhe " +
				"where bhe.borrower = ?", pUser);
	}

	public List<MovieInstance> findMovieInstancesBorrowedNowByUser(final User pUser) {
		CoreUtils.assertNotNullParameter(pUser, "pUser");	
		return getHibernateTemplate().find("from MovieInstance mi where mi.borrower = ?", pUser);
	}

	public List<MovieInstance> findMovieInstancesOwnedByUser(final User pUser) {
		CoreUtils.assertNotNullParameter(pUser, "pUser");		
		return getHibernateTemplate().find("from MovieInstance mi where mi.owner = ?", pUser);
	}

	public List<MovieInstance> findMovieInstancesWantedByUser(final User pUser) {
		CoreUtils.assertNotNullParameter(pUser, "pUser");	
		return getHibernateTemplate().find("select mi from MovieInstance mi inner join mi.waitListEntries wle " +
				"where wle.user = ?", pUser);
	}
}
