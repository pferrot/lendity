package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

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
	
	public Person findPersonFromUsername(final String username) {
		List<Person> list = getHibernateTemplate().find("from Person person where person.user.username = ?", username);
		if (list == null ||
			list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			return list.get(0);
		}
		else {
			throw new DataIntegrityViolationException("More that one person with username '" + username + "'");
		}
	}
}
