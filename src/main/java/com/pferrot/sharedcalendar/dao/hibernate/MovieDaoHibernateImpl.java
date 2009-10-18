package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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

	public List<Movie> findMoviesByTitle(final String pTitle, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pTitle);
		final String titleLower = pTitle.trim().toLowerCase();
		DetachedCriteria critera = DetachedCriteria.forClass(Movie.class).
			add(Restrictions.ilike("title", pTitle, MatchMode.ANYWHERE)).
			addOrder(Order.asc("title").ignoreCase());
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}
	
	public List<Movie> findMoviesByTitle(final String pTitle) {
		return findMoviesByTitle(pTitle, 0, 0);
	}	
	
	public List<Movie> findMovies(final int pFirstResult, final int pMaxResults) {
		DetachedCriteria critera = DetachedCriteria.forClass(Movie.class).
			addOrder(Order.asc("title").ignoreCase());
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}
	
	public List<Movie> findAllMovies() {
		return findMovies(0, 0);
	}

	public List<Movie> findMoviesOwnedByUser(final User pUser, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUser);
		return findMoviesOwnedByUser(pUser.getUsername(), pFirstResult, pMaxResults);
	}

	public List<Movie> findMoviesOwnedByUser(final String pUsername, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		DetachedCriteria critera = DetachedCriteria.forClass(Movie.class).
			addOrder(Order.asc("title").ignoreCase()).
			createCriteria("movieInstances", "mi", CriteriaSpecification.INNER_JOIN).
			createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("username", pUsername));
		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
//		return getHibernateTemplate().find("select m from Movie m join m.movieInstances mi where mi.owner.username = ? order by lower(m.title) asc", pUsername);
	}

	public List<Movie> findMoviesBorrowedByUser(final User pUser, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pUser);
		
		return findMoviesBorrowedByUser(pUser.getUsername(), pFirstResult, pMaxResults);
	}

	public List<Movie> findMoviesBorrowedByUser(final String pUsername, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		DetachedCriteria critera = DetachedCriteria.forClass(Movie.class).
			addOrder(Order.asc("title").ignoreCase()).
			createCriteria("movieInstances", "mi", CriteriaSpecification.INNER_JOIN).
			createCriteria("owner", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("username", pUsername));
		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
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
		CoreUtils.assertNotNull(pUser);
		return getHibernateTemplate().find("select mi from MovieInstance mi inner join mi.borrowerHistoryEntries bhe " +
				"where bhe.borrower = ?", pUser);
	}

	public List<MovieInstance> findMovieInstancesBorrowedNowByUser(final User pUser) {
		CoreUtils.assertNotNull(pUser);	
		return getHibernateTemplate().find("from MovieInstance mi where mi.borrower = ?", pUser);
	}

	public List<MovieInstance> findMovieInstancesOwnedByUser(final User pUser) {
		CoreUtils.assertNotNull(pUser);		
		return getHibernateTemplate().find("from MovieInstance mi where mi.owner = ?", pUser);
	}

	public List<MovieInstance> findMovieInstancesWantedByUser(final User pUser) {
		CoreUtils.assertNotNull(pUser);	
		return getHibernateTemplate().find("select mi from MovieInstance mi inner join mi.waitListEntries wle " +
				"where wle.user = ?", pUser);
	}
}
