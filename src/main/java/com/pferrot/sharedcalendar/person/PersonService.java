package com.pferrot.sharedcalendar.person;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
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
	
	public ListWithRowCount findEnabledPersons(final String pSearchString, final int pFirstResult, final int pMaxResults) {
		return personDao.findPersons(null, null, pSearchString, Boolean.TRUE, true, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findConnections(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		return personDao.findPersons(pPersonId, "connections", pSearchString, Boolean.TRUE, false, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findBannedPersons(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		return personDao.findPersons(pPersonId, "bannedPersons", pSearchString, Boolean.TRUE, false, pFirstResult, pMaxResults);
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
