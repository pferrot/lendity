package com.pferrot.lendity.person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.exception.PersonException;
import com.pferrot.lendity.utils.JsfUtils;

public class PersonService {

	private final static Log log = LogFactory.getLog(PersonService.class);
	
	private PersonDao personDao;
	private MailManager mailManager;

	public void setPersonDao(final PersonDao pPersonDao) {
		this.personDao = pPersonDao;
	}
	
	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public Person findPerson(final Long pPersonId) {
		return personDao.findPerson(pPersonId);
	}
	
	public Long createPerson(final Person pPerson) {
		return personDao.createPerson(pPerson);
	}

	public void updatePerson(final Person pPerson) {
		assertCurrentUserAuthorizedToEdit(pPerson);
		personDao.updatePerson(pPerson);
		PersonUtils.updatePersonInSession(pPerson, JsfUtils.getHttpServletRequest());
	}
	
	public ListWithRowCount findEnabledPersons(final String pSearchString, final int pFirstResult, final int pMaxResults) {
		return personDao.findPersons(null, PersonDao.UNSPECIFIED_LINK, pSearchString, Boolean.TRUE, true, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findConnections(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		return personDao.findPersons(pPersonId, PersonDao.CONNECTIONS_LINK, pSearchString, Boolean.TRUE, false, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findBannedPersons(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		// It is correct to search on the BANNED_BY_PERSONS_LINK: one actually search for that where ban by the current user.
		return personDao.findPersons(pPersonId, PersonDao.BANNED_BY_PERSONS_LINK, pSearchString, Boolean.TRUE, false, pFirstResult, pMaxResults);
	}

	/**
	 * Unban a user.
	 *
	 * @param pBannerPersonId
	 * @param pBannedPersonId
	 * @throws PersonException
	 */
	public void updateUnbanPerson(final Long pBannerPersonId, final Long pBannedPersonId) throws PersonException {
		try {
			CoreUtils.assertNotNull(pBannerPersonId);
			CoreUtils.assertNotNull(pBannedPersonId);
			
			final Person bannerPerson = personDao.findPerson(pBannerPersonId);
			assertCurrentUserAuthorizedToEdit(bannerPerson);
			final Person bannedPerson = personDao.findPerson(pBannedPersonId);
			if (bannedPerson != null) {
				bannerPerson.removeBannedPerson(bannedPerson);
				personDao.updatePerson(bannerPerson);
			}
		} 
		catch (Exception e) {
			throw new PersonException(e);
		}
	}
	
	/**
	 * Remove a connection. Note that no email is sent out.
	 *
	 * @param pConnectionRemoverId
	 * @param pConnectionToBeRemovedId
	 * @throws PersonException
	 */
	public void updateRemoveConnection(final Long pConnectionRemoverId, final Long pConnectionToBeRemovedId) throws PersonException {
		try {
			CoreUtils.assertNotNull(pConnectionRemoverId);
			CoreUtils.assertNotNull(pConnectionToBeRemovedId);
			
			final Person connectionRemover = personDao.findPerson(pConnectionRemoverId);
			assertCurrentUserAuthorizedToEdit(connectionRemover);
			final Person connectionToBeRemoved = personDao.findPerson(pConnectionToBeRemovedId);
			if (connectionToBeRemoved != null) {
				connectionRemover.removeConnection(connectionToBeRemoved);
				personDao.updatePerson(connectionRemover);
				personDao.updatePerson(connectionToBeRemoved);
			}
		} 
		catch (Exception e) {
			throw new PersonException(e);
		}
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

	public void assertCurrentUserAuthorizedToView(final Person pPerson) {
		if (!isCurrentUserAuthorizedToView(pPerson)) {
			throw new SecurityException("Current user is not authorized to view person");
		}
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
	
	public void assertCurrentUserAuthorizedToEdit(final Person pPerson) {
		if (!isCurrentUserAuthorizedToEdit(pPerson)) {
			throw new SecurityException("Current user is not authorized to edit person");
		}
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
