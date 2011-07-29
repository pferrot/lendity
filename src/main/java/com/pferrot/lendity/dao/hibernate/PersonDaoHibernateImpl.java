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
import org.hibernate.type.Type;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.PersonDaoQueryBean;
import com.pferrot.lendity.dao.hibernate.criterion.CustomSqlCriterion;
import com.pferrot.lendity.dao.hibernate.criterion.OrderBySql;
import com.pferrot.lendity.geolocation.GeoLocationConsts;
import com.pferrot.lendity.geolocation.GeoLocationUtils;
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

	public Person findPersonFromDisplayName(final String pDisplayName) {
		CoreUtils.assertNotNullOrEmptyString(pDisplayName);
		List<Person> list = getHibernateTemplate().find("from Person person where person.displayName = ?", pDisplayName);		
		
		if (list == null ||
			list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			return list.get(0);
		}
		else {
			throw new DataIntegrityViolationException("More that one person with display name '" + pDisplayName + "'");
		}
	}

	public List<Person> findPersonsList(PersonDaoQueryBean pQueryBean) {
		DetachedCriteria criteria = getPersonsDetachedCriteria(pQueryBean);
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		if (!StringUtils.isNullOrEmpty(pQueryBean.getOrderBy())) {
			if ("random".equals(pQueryBean.getOrderBy())) {
				// This is MySql specific !!!
				criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
			}
			else if ("distance".equals(pQueryBean.getOrderBy())) {
				final String ascOrDesc = Boolean.TRUE.equals(pQueryBean.getOrderByAscending())?"asc":"desc";
				if (pQueryBean.getOriginLatitude() != null && pQueryBean.getOriginLongitude() != null) {
					// First order to make sure that persons where the distance cannot be calculated appear last.
					// Only check the latitude to simplify the query. If one of latitude/longitude is null, both should be null.
					criteria.addOrder(OrderBySql.sql("{0} is not null desc", new String[] {"addressHomeLatitude"}));
					// Actual sorting by distance.
					criteria.addOrder(
							OrderBySql.sql("(" + GeoLocationUtils.getDistanceFormula(pQueryBean.getOriginLongitude(), pQueryBean.getOriginLatitude()) + ") " + ascOrDesc,
												         new String[] {"addressHomeLatitude", "addressHomeLatitude", "addressHomeLongitude"})
					    );
				}	
				else {
					throw new RuntimeException("Cannot sort by distance when origin latitude/longitude are not defined");
				}
				
			}
			else {
				// Ascending.
				if (pQueryBean.getOrderByAscending() == null || pQueryBean.getOrderByAscending().booleanValue()) {
					criteria.addOrder(Order.asc(pQueryBean.getOrderBy()).ignoreCase());
				}
				// Descending.
				else {
					criteria.addOrder(Order.desc(pQueryBean.getOrderBy()).ignoreCase());
				}
			}
		}
		
		
//		if (pOriginLongitude != null && pOriginLatitude != null) {
//			criteria.addOrder(
//						OrderBySql.sql("(" + getDistanceFormula(pOriginLongitude, pOriginLatitude) + ") asc",
//											         new String[] {"addressHomeLatitude", "addressHomeLatitude", "addressHomeLongitude"})
//				    );
//		}	
		
		// Cache query for getting connections as it can be used many times in a page.
		if (pQueryBean.getConnectionLink() == CONNECTIONS_LINK) {
			getHibernateTemplate().setQueryCacheRegion("query.connections");
			getHibernateTemplate().setCacheQueries(true);
		}
		return getHibernateTemplate().findByCriteria(criteria, pQueryBean.getFirstResult(), pQueryBean.getMaxResults());
	}
	
	/**
	 * @deprecated
	 */
	public List<Person> findPersonsList(final Long pPersonId, final int pConnectionLink, final String pSearchString,
			final Boolean pEmailExactMatch, Boolean pEnabled, final Boolean pReceiveNeedsNotifications, final Boolean pEmailSubscriber, final Date pEmailSubscriberLastUpdateMax,
			final Double pMaxDistanceKm, final Double pOriginLatitude, final Double pOriginLongitude,
			int pFirstResult, int pMaxResults) {
		
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		queryBean.setPersonId(pPersonId);
		queryBean.setConnectionLink(pConnectionLink);
		queryBean.setSearchString(pSearchString);
		queryBean.setEmailExactMatch(pEmailExactMatch);
		queryBean.setEnabled(pEnabled);
		queryBean.setReceiveNeedsNotifications(pReceiveNeedsNotifications);
		queryBean.setEmailSubscriber(pEmailSubscriber);
		queryBean.setEmailSubscriberLastUpdateMax(pEmailSubscriberLastUpdateMax);
		queryBean.setMaxDistanceKm(pMaxDistanceKm);
		queryBean.setOriginLatitude(pOriginLatitude);
		queryBean.setOriginLongitude(pOriginLongitude);
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		
		return findPersonsList(queryBean);
	}
	
	public long countPersons(final PersonDaoQueryBean pQueryBean) {
		final DetachedCriteria criteria = getPersonsDetachedCriteria(pQueryBean);
		return rowCount(criteria);
	}

	public ListWithRowCount findPersons(final PersonDaoQueryBean pQueryBean) {
		final List list = findPersonsList(pQueryBean);
		final long count = countPersons(pQueryBean);

		return new ListWithRowCount(list, count);
	}
	
	/**
	 * @deprecated
	 */
	public ListWithRowCount findPersons(final Long pPersonId, final int pConnectionLink, final String pSearchString, final Boolean pEmailExactMatch,
			final Boolean pEnabled, final Boolean pReceiveNeedsNotifications, final Boolean pEmailSubscriber, final Date pEmailSubscriberLastUpdateMax,
			final Double pMaxDistanceKm, final Double pOriginLatitude, final Double pOriginLongitude,
			int pFirstResult, int pMaxResults) {
		
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		queryBean.setPersonId(pPersonId);
		queryBean.setConnectionLink(pConnectionLink);
		queryBean.setSearchString(pSearchString);
		queryBean.setEmailExactMatch(pEmailExactMatch);
		queryBean.setEnabled(pEnabled);
		queryBean.setReceiveNeedsNotifications(pReceiveNeedsNotifications);
		queryBean.setEmailSubscriber(pEmailSubscriber);
		queryBean.setEmailSubscriberLastUpdateMax(pEmailSubscriberLastUpdateMax);
		queryBean.setMaxDistanceKm(pMaxDistanceKm);
		queryBean.setOriginLatitude(pOriginLatitude);
		queryBean.setOriginLongitude(pOriginLongitude);
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		
		return findPersons(queryBean);
	}

	private DetachedCriteria getPersonsDetachedCriteria(final PersonDaoQueryBean pQueryBean) {
		if ((pQueryBean.getPersonId() == null && pQueryBean.getConnectionLink() != PersonDao.UNSPECIFIED_LINK) ||
			(pQueryBean.getPersonId() != null && pQueryBean.getConnectionLink() == PersonDao.UNSPECIFIED_LINK)) {
			throw new AssertionError("Cannot define only one of 'pPersonId' and 'pConnectionLink'.");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Person.class);
		
		if (pQueryBean.getSearchString() != null && pQueryBean.getSearchString().trim().length() > 0) {
			criteria.add(getEmailLastNameFirstNameDisplayNameLogicalExpression(pQueryBean));
		}
		
		if (pQueryBean.getEmail() != null && pQueryBean.getEmail().trim().length() > 0) {
			criteria.add(Restrictions.ilike("email", pQueryBean.getEmail(), MatchMode.EXACT));
		}
		
		if (pQueryBean.getPersonId() != null) {			
			String realConnectionLink = null;
			switch (pQueryBean.getConnectionLink()) {
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
					throw new AssertionError("Unknown connection link: " + pQueryBean.getConnectionLink());
			}			
			criteria.createCriteria(realConnectionLink, CriteriaSpecification.INNER_JOIN).
				add(Restrictions.eq("id", pQueryBean.getPersonId()));
		}
		
		if (pQueryBean.getPersonToIgnoreId() != null) {
			criteria.add(Restrictions.not(Restrictions.eq("id", pQueryBean.getPersonToIgnoreId())));
		}
		
		if (pQueryBean.getEnabled() != null) {
			criteria.add(Restrictions.eq("enabled", pQueryBean.getEnabled()));
		}
		
		if (pQueryBean.getReceiveNeedsNotifications() != null) {
			criteria.add(Restrictions.eq("receiveNeedsNotifications", pQueryBean.getReceiveNeedsNotifications()));
		}
		
		if (pQueryBean.getEmailSubscriber() != null) {
			criteria.add(Restrictions.eq("emailSubscriber", pQueryBean.getEmailSubscriber()));
		}
		
		if (pQueryBean.getEmailSubscriberLastUpdateMax() != null) {
			criteria.add(Restrictions.or(
					Restrictions.lt("emailSubscriberLastUpdate", pQueryBean.getEmailSubscriberLastUpdateMax()),
					Restrictions.isNull("emailSubscriberLastUpdate")
				)); 
		}
		
		if (pQueryBean.getMaxDistanceKm() != null) {
			
			// Only allows filtering by distance on people who share their contact details.
			//criteria.add(Restrictions.eq("showContactDetailsToAll", Boolean.TRUE));
			
			// Calculate a rectangle and only consider persons in that rectangle for obvious performance reason.
			double maxDistanceKm = pQueryBean.getMaxDistanceKm().doubleValue();
			double originLatitude = pQueryBean.getOriginLatitude().doubleValue();
			double originLongitude = pQueryBean.getOriginLongitude().doubleValue();
			
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
			final String sql = "(" + GeoLocationUtils.getDistanceFormula("?", "?", "?") + ") <= ?";
			
			final String[] propertyNames = {"addressHomeLatitude", "addressHomeLatitude", "addressHomeLongitude"};
			final Object[] values = {pQueryBean.getOriginLatitude(), pQueryBean.getOriginLatitude(), pQueryBean.getOriginLongitude(), pQueryBean.getMaxDistanceKm()};
			final Type[] types = {Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE};
			
			criteria.add(CustomSqlCriterion.sqlRestriction(sql, propertyNames, values, types));
		}
		
		return criteria;
	}

	private LogicalExpression getEmailLastNameFirstNameDisplayNameLogicalExpression(final PersonDaoQueryBean pQueryBean) {
		CoreUtils.assertNotNull(pQueryBean);
		CoreUtils.assertNotNullOrEmptyString(pQueryBean.getSearchString());
		
		final boolean emailExactMatch = Boolean.TRUE.equals(pQueryBean.getEmailExactMatch());
		final MatchMode emailMatchMode = emailExactMatch?MatchMode.EXACT:MatchMode.ANYWHERE;
	
		
		final Criterion emailCriterion = Restrictions.ilike("email", pQueryBean.getSearchString(), emailMatchMode);
		// Display name not needed since 
		final Criterion displayNameCriterion = Restrictions.ilike("displayName", pQueryBean.getSearchString(), MatchMode.ANYWHERE);
		// Do not search on first name or last name when email exact match.
		if (emailExactMatch) {
			return Restrictions.or(displayNameCriterion, emailCriterion);
		}
		else {
			// First name and last name not needed since contained in display name.
			final Criterion firstNameCriterion = Restrictions.ilike("firstName", pQueryBean.getSearchString(), MatchMode.ANYWHERE);
			final Criterion lastNameCriterion = Restrictions.ilike("lastName", pQueryBean.getSearchString(), MatchMode.ANYWHERE);
			return Restrictions.or(displayNameCriterion, 
					   Restrictions.or(emailCriterion, 
							Restrictions.or(firstNameCriterion, lastNameCriterion)));
		}
		
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

	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Get members / administrators / banned of a group.
	
	public long countGroupAdministrators(final Long pGroupId) {
		final DetachedCriteria dc = getGroupAdministratorsDetachedCriteria(pGroupId);
		final long count = rowCount(dc);
		return count;
	}

	public long countGroupMembers(final Long pGroupId) {
		final DetachedCriteria dc = getGroupMembersDetachedCriteria(pGroupId);
		final long count = rowCount(dc);
		return count;
	}
	
	public long countGroupBanned(final Long pGroupId) {
		final DetachedCriteria dc = getGroupBannedDetachedCriteria(pGroupId);
		final long count = rowCount(dc);
		return count;
	}

	public ListWithRowCount findGroupAdministrators(final Long pGroupId, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria dc = getGroupAdministratorsDetachedCriteria(pGroupId);
		dc.addOrder(Order.asc("displayName").ignoreCase());
		final List list = getHibernateTemplate().findByCriteria(dc, pFirstResult, pMaxResults);
		final long count = rowCount(dc);

		return new ListWithRowCount(list, count);
	}

	public ListWithRowCount findGroupMembers(final Long pGroupId, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria dc = getGroupMembersDetachedCriteria(pGroupId);
		dc.addOrder(Order.asc("displayName").ignoreCase());
		final List list = getHibernateTemplate().findByCriteria(dc, pFirstResult, pMaxResults);
		final long count = rowCount(dc);

		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findGroupBanned(final Long pGroupId, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria dc = getGroupBannedDetachedCriteria(pGroupId);
		dc.addOrder(Order.asc("displayName").ignoreCase());
		final List list = getHibernateTemplate().findByCriteria(dc, pFirstResult, pMaxResults);
		final long count = rowCount(dc);

		return new ListWithRowCount(list, count);
	}
	
	private DetachedCriteria getGroupMembersDetachedCriteria(final Long pGroupId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Person.class);
		
		final DetachedCriteria groupsMemberCriteria = criteria.createCriteria("groupsMember", CriteriaSpecification.INNER_JOIN);
		groupsMemberCriteria.add(Restrictions.eq("id",pGroupId));
		
		return criteria;
	}
	
	private DetachedCriteria getGroupAdministratorsDetachedCriteria(final Long pGroupId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Person.class);
		
		final DetachedCriteria groupsMemberCriteria = criteria.createCriteria("groupsAdministrator", CriteriaSpecification.INNER_JOIN);
		groupsMemberCriteria.add(Restrictions.eq("id", pGroupId));
		
		return criteria;
	}
	
	private DetachedCriteria getGroupBannedDetachedCriteria(final Long pGroupId) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(Person.class);
		
		final DetachedCriteria groupsMemberCriteria = criteria.createCriteria("groupsBanned", CriteriaSpecification.INNER_JOIN);
		groupsMemberCriteria.add(Restrictions.eq("id", pGroupId));
		
		return criteria;
	}
}