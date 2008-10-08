package com.pferrot.sharedcalendar.dao.hibernate;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Person;

public class PersonDaoHibernateImpl extends HibernateDaoSupport implements PersonDao {

	public Integer createPerson(Person person) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deletePerson(Person person) {
		// TODO Auto-generated method stub
		
	}

	public Person findPerson(Integer personId) {
		return (Person)getHibernateTemplate().load(Person.class, personId);
	}

	public void updatePerson(Person person) {
		// TODO Auto-generated method stub
		
	}

	
}
