package com.pferrot.sharedcalendar.person;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.utils.JsfUtils;

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
		PersonUtils.updatePersonInSession(pPerson, JsfUtils.getHttpServletRequest());
	}
	
	public List<Person> findPersonsByAnything(final String pSearchString, final int pFirstResult, final int pMaxResults) {
		return personDao.findPersonByAnything(pSearchString, pFirstResult, pMaxResults);
	}

	public List<Person> findCurrentUserConnectionsByAnything(final String pSearchString, final int pFirstResult, final int pMaxResults) {
		return personDao.findConnectionsByAnything(pSearchString, getCurrentPerson(), pFirstResult, pMaxResults);
	}

	public List<Person> findCurrentUserConnections(final int pFirstResult, final int pMaxResults) {	
		return personDao.findConnections(getCurrentPerson(), pFirstResult, pMaxResults);
	}

	public List<Person> findCurrentUserBannedPersonsByAnything(final String pSearchString, final int pFirstResult, final int pMaxResults) {
		return personDao.findBannedPersonsByAnything(pSearchString, getCurrentPerson(), pFirstResult, pMaxResults);
	}

	public List<Person> findCurrentUserBannedPersons(final int pFirstResult, final int pMaxResults) {	
		return personDao.findBannedPersons(getCurrentPerson(), pFirstResult, pMaxResults);
	}
	
	public boolean isCurrentUserAuthorizedToView(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		if (isCurrentUserAuthorizedToEdit(pPerson)) {
			return true;
		}
		// Connections can view.
		if (pPerson.getConnections().contains(getCurrentPerson())) {
			return true;
		}
		return false;
	}

	public boolean isCurrentUserAuthorizedToEdit(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		if (currentPerson.equals(pPerson)) {
			return true;
		}
		if (currentPerson.getUser() != null &&
		    currentPerson.getUser().isAdmin()) {
			return true;
		}
		return false;
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		if (currentPerson.getUser() != null &&
		    currentPerson.getUser().isAdmin()) {
			return true;
		}
		return false;
	}

	private Person getCurrentPerson() {
//		final String username = SecurityUtils.getCurrentUsername();
//		return personDao.findPersonFromUsername(username);
		return personDao.findPerson(PersonUtils.getCurrentPersonId());
	}
}
