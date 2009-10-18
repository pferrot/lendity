package com.pferrot.sharedcalendar.person;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Person;

public class PersonService {

	private final static Log log = LogFactory.getLog(PersonService.class);
	
	private PersonDao personDao;

	public void setPersonDao(final PersonDao pPersonDao) {
		this.personDao = pPersonDao;
	}
	
	public Person findPerson(final Long pPersonId) {
		return personDao.findPerson(pPersonId);
	}
	
	public Long createPerson(final Person pPerson) {
		return personDao.createPerson(pPerson);
	}

	public void updatePerson(final Person pPerson) {
		personDao.updatePerson(pPerson);
	}
	
	public List<Person> findPersonsByAnything(final String pSearchString, final int pFirstResult, final int pMaxResults) {
		return personDao.findPersonByAnything(pSearchString, pFirstResult, pMaxResults);
	}
}
