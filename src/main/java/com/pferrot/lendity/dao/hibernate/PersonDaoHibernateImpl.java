package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
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
	private List<Person> findPersonsList(final Long pPersonId, final String pConnectionLink, final String pSearchString, final Boolean pEnabled, boolean pEmailExactMatch, int pFirstResult, int pMaxResults) {
		final DetachedCriteria criteria = getPersonsDetachedCriteria(pPersonId, pConnectionLink, pSearchString, pEnabled, pEmailExactMatch).addOrder(Order.asc("displayName").ignoreCase());
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}
	
	private long countPersons(final Long pPersonId, final String pConnectionLink, final String pSearchString, final Boolean pEnabled, boolean pEmailExactMatch) {
		final DetachedCriteria criteria = getPersonsDetachedCriteria(pPersonId, pConnectionLink, pSearchString, pEnabled, pEmailExactMatch).addOrder(Order.asc("displayName").ignoreCase());
		return rowCount(criteria);
	}

	public ListWithRowCount findPersons(final Long pPersonId, final String pConnectionLink, final String pSearchString, final Boolean pEnabled, final boolean pEmailExactMatch, int pFirstResult, int pMaxResults) {
		final List list = findPersonsList(pPersonId, pConnectionLink, pSearchString, pEnabled, pEmailExactMatch, pFirstResult, pMaxResults);
		final long count = countPersons(pPersonId, pConnectionLink, pSearchString, pEnabled, pEmailExactMatch);

		return new ListWithRowCount(list, count);
	}

	private DetachedCriteria getPersonsDetachedCriteria(final Long pPersonId, final String pConnectionLink, final String pSearchString, final Boolean pEnabled, boolean pEmailExactMatch) {
		if ((pPersonId == null && pConnectionLink != null) ||
			(pPersonId != null && pConnectionLink == null)) {
			throw new AssertionError("Cannot define only one of 'pPersonId' and 'pConnectionLink'.");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Person.class);
		
		if (pSearchString != null && pSearchString.trim().length() > 0) {
			criteria.add(getEmailLastNameFirstNameDisplayNameLogicalExpression(pSearchString, pEmailExactMatch?MatchMode.EXACT:MatchMode.ANYWHERE));
		}
		
		if (pPersonId != null) {
			criteria.createCriteria(pConnectionLink, CriteriaSpecification.INNER_JOIN).
				add(Restrictions.eq("id", pPersonId));
		}
		
		if (pEnabled != null) {
			criteria.add(Restrictions.eq("enabled", pEnabled));
		}
		
		return criteria;
	}

	private LogicalExpression getEmailLastNameFirstNameDisplayNameLogicalExpression(final String pSearchString, final MatchMode pEmailMatchMode) {
		CoreUtils.assertNotNullOrEmptyString(pSearchString);
	
		// First name and last name not needed since contained in display name.
//		final Criterion firstNameCriterion = Restrictions.ilike("firstName", pSearchString, MatchMode.ANYWHERE);
//		final Criterion lastNameCriterion = Restrictions.ilike("lastName", pSearchString, MatchMode.ANYWHERE);
		
		final Criterion emailCriterion = Restrictions.ilike("email", pSearchString,pEmailMatchMode);
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