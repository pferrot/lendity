package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.sharedcalendar.dao.MovieDao;
import com.pferrot.sharedcalendar.model.ListValue;
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

	public List<Movie> findMoviesByTitle(final String title) {
		if (title == null || title.trim().length() == 0) {
			throw new IllegalArgumentException("'title' parameter must not be null or empty");
		}
		final String titleLower = title.trim().toLowerCase();
		return getHibernateTemplate(). find("from Movie m where lower(m.title) like '%" + titleLower + "%'");
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	// MovieInstance
	public Long createMovieInstance(MovieInstance movieInstance) {
		return (Long)getHibernateTemplate().save(movieInstance);
	}

	public void deleteMovieInstance(Movie movieInstance) {
		getHibernateTemplate().delete(movieInstance);		
	}

	public void updateMovieInstance(MovieInstance movieInstance) {
		getHibernateTemplate().update(movieInstance);		
	}
	
	public MovieInstance findMovieInstance(Long movieInstanceId) {
		return (MovieInstance)getHibernateTemplate().load(MovieInstance.class, movieInstanceId);
	}	
}
