package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Person;

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
	
	public Person findPersonFromUser(final User pUser) {
		CoreUtils.assertNotNull(pUser);
		return findPersonFromUsername(pUser.getUsername());
	}	

	public List<Person> findPersonByAnything(final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNullOrEmptyString(pSearchString);
		
		DetachedCriteria critera = DetachedCriteria.forClass(Person.class).
		                  add(getEmailLastNameFirstNameLogicalExpression(pSearchString)).
		                  addOrder(Order.asc("lastName").ignoreCase());
		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}

	public List<Person> findPersonByAnything(final String pSearchString) {
		return findPersonByAnything(pSearchString, 0, 0);
	}

	public List<Person> findConnectionsByAnything(final String pSearchString, final Person pPerson, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPerson);

		DetachedCriteria criteria = DetachedCriteria.forClass(Person.class).
			addOrder(Order.asc("lastName").ignoreCase());
		
		if (pSearchString != null && pSearchString.trim().length() > 0) {
			criteria.add(getEmailLastNameFirstNameLogicalExpression(pSearchString));
		}
		criteria.createCriteria("connections", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("id", pPerson.getId()));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}

	public List<Person> findConnections(final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findConnectionsByAnything(null, pPerson, pFirstResult, pMaxResults);
	}	

	public List<Person> findBannedPersonsByAnything(String pSearchString,
			Person pPerson, int pFirstResult, int pMaxResults) {
		CoreUtils.assertNotNull(pPerson);

		DetachedCriteria criteria = DetachedCriteria.forClass(Person.class).
			addOrder(Order.asc("lastName").ignoreCase());
		
		if (pSearchString != null && pSearchString.trim().length() > 0) {
			criteria.add(getEmailLastNameFirstNameLogicalExpression(pSearchString));
		}
		criteria.createCriteria("bannedPersons", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("id", pPerson.getId()));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);
	}
	
	public List<Person> findBannedPersons(Person pPerson, int pFirstResult,
			int pMaxResults) {
		return findBannedPersonsByAnything(null, pPerson, 0, 0);
	}

	private LogicalExpression getEmailLastNameFirstNameLogicalExpression(final String pSearchString) {
		CoreUtils.assertNotNullOrEmptyString(pSearchString);
		
		final Criterion firstNameCriterion = Restrictions.ilike("firstName", pSearchString, MatchMode.ANYWHERE);
		final Criterion lastNameCriterion = Restrictions.ilike("lastName", pSearchString, MatchMode.ANYWHERE);
		final Criterion emailCriterion = Restrictions.ilike("email", pSearchString, MatchMode.ANYWHERE);
		return Restrictions.or(firstNameCriterion, Restrictions.or(lastNameCriterion, emailCriterion));
	}
}