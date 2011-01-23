package com.pferrot.lendity.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.hibernate.type.Type;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.hibernate.criterion.CustomSqlCriterion;
import com.pferrot.lendity.geolocation.GeoLocationConsts;
import com.pferrot.lendity.model.Person;

public class PersonDaoHibernateImpl extends HibernateDaoSupport implements PersonDao {

	public Long createPerson(final Person person) {
		return (Long)getHibernateTemplate().save(person);
	}

	public void deletePerson(final Person person) {
		getHibernateTemplate().delete(person);
	}

	public void updatePerson(final Person person) {
		getHibernateTemplate().update(person);
	}

	public Person findPerson(final Long personId) {
		return (Person)getHibernateTemplate().load(Person.class, personId);
	}
	
	public Person findPersonFromUsername(final String pUsername) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		List<Person> list = getHibernateTemplate().find("from Person person where person.user.username = ?", pUsername);		
		
		if (list == null ||
			list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			return list.get(0);
		}
		else {
			throw new DataIntegrityViolationException("More that one person with username '" + pUsername + "'");
		}
	}
	
	public List<Person> findPersonsList(final Long pPersonId, final int pConnectionLink, final String pSearchString,
			final Boolean pEmailExactMatch, Boolean pEnabled, final Boolean pReceiveNeedsNotifications, final Boolean pEmailSubscriber, final Date pEmailSubscriberLastUpdateMax,
			final Double pMaxDistanceKm, final Double pOriginLatitude, final Double pOriginLongitude,
			int pFirstResult, int pMaxResults) {
		DetachedCriteria criteria = getPersonsDetachedCriteria(pPersonId, pConnectionLink, pSearchString, pEmailExactMatch, 
				pEnabled, pReceiveNeedsNotifications, pEmailSubscriber, pEmailSubscriberLastUpdateMax,
				pMaxDistanceKm, pOriginLatitude, pOriginLongitude);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.addOrder(Order.asc("displayName").ignoreCase());
		
//		if (pOriginLongitude != null && pOriginLatitude != null) {
//			criteria.addOrder(
//						OrderBySql.sql("(" + getDistanceFormula(pOriginLongitude, pOriginLatitude) + ") asc",
//											         new String[] {"addressHomeLatitude", "addressHomeLatitude", "addressHomeLongitude"})
//				    );
//		}	
		
		// Cache query for getting connections as it can be used many times in a page.
		if (pConnectionLink == CONNECTIONS_LINK) {
			getHibernateTemplate().setQueryCacheRegion("query.connections");
			getHibernateTemplate().setCacheQueries(true);
		}
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

//	private String getDistanceFormula(final Double pOriginLongitude, final Double pOriginLatitude) {
//		
//		return "acos(sin(" + pOriginLatitude + " * pi()/180) * sin({0} * pi()/180) + " +
//				"cos(" + pOriginLatitude + " * pi()/180) * cos({1} * pi()/180) * cos(({2} - " + pOriginLongitude + ") * pi()/180)) * 6371";
//		
//	}
	
	private long countPersons(final Long pPersonId, final int pConnectionLink, final String pSearchString, final Boolean pEmailExactMatch,
			final Boolean pEnabled, final Boolean pReceiveNeedsNotifications, final Boolean pEmailSubscriber, final Date pEmailSubscriberLastUpdateMax,
			final Double pMaxDistanceKm, final Double pOriginLatitude, final Double pOriginLongitude) {
		final DetachedCriteria criteria = getPersonsDetachedCriteria(pPersonId, pConnectionLink, pSearchString, pEmailExactMatch,
				pEnabled, pReceiveNeedsNotifications, pEmailSubscriber, pEmailSubscriberLastUpdateMax, pMaxDistanceKm, pOriginLatitude, pOriginLongitude);
		return rowCount(criteria);
	}

	public ListWithRowCount findPersons(final Long pPersonId, final int pConnectionLink, final String pSearchString, final Boolean pEmailExactMatch,
			final Boolean pEnabled, final Boolean pReceiveNeedsNotifications, final Boolean pEmailSubscriber, final Date pEmailSubscriberLastUpdateMax,
			final Double pMaxDistanceKm, final Double pOriginLatitude, final Double pOriginLongitude,
			int pFirstResult, int pMaxResults) {
		final List list = findPersonsList(pPersonId, pConnectionLink, pSearchString, pEmailExactMatch, pEnabled,
				pReceiveNeedsNotifications, pEmailSubscriber, pEmailSubscriberLastUpdateMax, pMaxDistanceKm, pOriginLatitude, pOriginLongitude, pFirstResult, pMaxResults);
		final long count = countPersons(pPersonId, pConnectionLink, pSearchString, pEmailExactMatch, pEnabled,
				pReceiveNeedsNotifications, pEmailSubscriber, pEmailSubscriberLastUpdateMax, pMaxDistanceKm, pOriginLatitude, pOriginLongitude);

		return new ListWithRowCount(list, count);
	}

	private DetachedCriteria getPersonsDetachedCriteria(final Long pPersonId, final int pConnectionLink, final String pSearchString,
			final Boolean pEmailExactMatch, final Boolean pEnabled, final Boolean pReceiveNeedsNotifications, final Boolean pEmailSubscriber, final Date pEmailSubscriberLastUpdateMax,
			final Double pMaxDistanceKm, final Double pOriginLatitude, final Double pOriginLongitude) {
		if ((pPersonId == null && pConnectionLink != PersonDao.UNSPECIFIED_LINK) ||
			(pPersonId != null && pConnectionLink == PersonDao.UNSPECIFIED_LINK)) {
			throw new AssertionError("Cannot define only one of 'pPersonId' and 'pConnectionLink'.");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Person.class);
		
		if (pSearchString != null && pSearchString.trim().length() > 0) {
			final boolean emailExactMatch = Boolean.TRUE.equals(pEmailExactMatch);
			criteria.add(getEmailLastNameFirstNameDisplayNameLogicalExpression(pSearchString, emailExactMatch?MatchMode.EXACT:MatchMode.ANYWHERE));
		}
		
		if (pPersonId != null) {			
			String realConnectionLink = null;
			switch (pConnectionLink) {
				case PersonDao.CONNECTIONS_LINK:
					realConnectionLink = "connections";
					break;
				case PersonDao.BANNED_PERSONS_LINK:
					realConnectionLink = "bannedPersons";
					break;
				case PersonDao.BANNED_BY_PERSONS_LINK:
					realConnectionLink = "bannedByPersons";
					break;
				default:
					throw new AssertionError("Unknown connection link: " + pConnectionLink);
			}			
			criteria.createCriteria(realConnectionLink, CriteriaSpecification.INNER_JOIN).
				add(Restrictions.eq("id", pPersonId));
		}
		
		if (pEnabled != null) {
			criteria.add(Restrictions.eq("enabled", pEnabled));
		}
		
		if (pReceiveNeedsNotifications != null) {
			criteria.add(Restrictions.eq("receiveNeedsNotifications", pReceiveNeedsNotifications));
		}
		
		if (pEmailSubscriber != null) {
			criteria.add(Restrictions.eq("emailSubscriber", pEmailSubscriber));
		}
		
		if (pEmailSubscriberLastUpdateMax != null) {
			criteria.add(Restrictions.or(
					Restrictions.lt("emailSubscriberLastUpdate", pEmailSubscriberLastUpdateMax),
					Restrictions.isNull("emailSubscriberLastUpdate")
				)); 
		}
		
		if (pMaxDistanceKm != null) {
			
			// Only allows filtering by distance on people who share their contact details.
			criteria.add(Restrictions.eq("showContactDetailsToAll", Boolean.TRUE));
			
			// Calculate a rectangle and only consider persons in that rectangle for obvious performance reason.
			double maxDistanceKm = pMaxDistanceKm.doubleValue();
			double originLatitude = pOriginLatitude.doubleValue();
			double originLongitude = pOriginLongitude.doubleValue();
			
			double deltaLongitude =  maxDistanceKm / Math.abs(Math.cos(Math.toRadians(originLatitude)) * GeoLocationConsts.ONE_DEGRE_LATITUDE_KM);
			double deltaLatitude = maxDistanceKm / GeoLocationConsts.ONE_DEGRE_LATITUDE_KM;
			
			double longitude1 = originLongitude - deltaLongitude;
			double longitude2 = originLongitude + deltaLongitude;
			
			double latitude1 = originLatitude - deltaLatitude; 
			double latitude2 = originLatitude + deltaLatitude;
			
			// Pre-filtering for performance reason. Records not in that square do not need
			// to be considered. 
			criteria.add(Restrictions.ge("addressHomeLatitude", Double.valueOf(latitude1)));
			criteria.add(Restrictions.le("addressHomeLatitude", Double.valueOf(latitude2)));
			criteria.add(Restrictions.ge("addressHomeLongitude", Double.valueOf(longitude1)));
			criteria.add(Restrictions.le("addressHomeLongitude", Double.valueOf(longitude2)));
			
			
			// Fine grain the result - calculate the exact distance.
			// It would be too expensive to do that for all records, that is why we pre-filter with
			// a square above.
			final String sql = "(acos(sin(? * pi()/180) * sin({0} * pi()/180) + " +
				"cos(? * pi()/180) * cos({1} * pi()/180) * cos(({2} - ?) * pi()/180)) * 6371) <= ?";
			
			final String[] propertyNames = {"addressHomeLatitude", "addressHomeLatitude", "addressHomeLongitude"};
			final Object[] values = {pOriginLatitude, pOriginLatitude, pOriginLongitude, pMaxDistanceKm};
			final Type[] types = {Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE};
			
			criteria.add(CustomSqlCriterion.sqlRestriction(sql, propertyNames, values, types));
		}
		
		return criteria;
	}

	private LogicalExpression getEmailLastNameFirstNameDisplayNameLogicalExpression(final String pSearchString, final MatchMode pEmailMatchMode) {
		CoreUtils.assertNotNullOrEmptyString(pSearchString);
	
		// First name and last name not needed since contained in display name.
//		final Criterion firstNameCriterion = Restrictions.ilike("firstName", pSearchString, MatchMode.ANYWHERE);
//		final Criterion lastNameCriterion = Restrictions.ilike("lastName", pSearchString, MatchMode.ANYWHERE);
		
		final Criterion emailCriterion = Restrictions.ilike("email", pSearchString, pEmailMatchMode);
		// Display name not needed since 
		final Criterion displayNameCriterion = Restrictions.ilike("displayName", pSearchString, MatchMode.ANYWHERE);
		return Restrictions.or(displayNameCriterion, emailCriterion);
	}

	/**
	 * Returns the number of rows for a giver DetachedCriteria.
	 *
	 * @param pCriteria
	 * @return
	 */
	private long rowCount(final DetachedCriteria pCriteria) {
		pCriteria.setProjection(Projections.rowCount());
		return ((Long)getHibernateTemplate().findByCriteria(pCriteria).get(0)).longValue();
	}
}