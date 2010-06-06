package com.pferrot.sharedcalendar.person;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.sharedcalendar.configuration.Configuration;
import com.pferrot.sharedcalendar.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.exception.PersonException;
import com.pferrot.sharedcalendar.utils.JsfUtils;

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

	/**
	 * Unban a user and send an email to the person who was banned.
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
			final Person bannedPerson = personDao.findPerson(pBannedPersonId);
			if (bannedPerson != null) {
				bannerPerson.removeBannedPerson(bannedPerson);
				personDao.updatePerson(bannerPerson);
				
				// Send email (will actually create a JMS message, i.e. it is async).
				Map<String, String> objects = new HashMap<String, String>();
				objects.put("bannedFirstName", bannedPerson.getFirstName());
				objects.put("userFirstName", bannerPerson.getFirstName());
				objects.put("userLastName", bannerPerson.getLastName());
				objects.put("signature", Configuration.getSiteName());
				objects.put("siteName", Configuration.getSiteName());
				
				// TODO: localization
				final String velocityTemplateLocation = "com/pferrot/sharedcalendar/emailtemplate/connectionrequest/unban/en";
				
				Map<String, String> to = new HashMap<String, String>();
				to.put(bannedPerson.getEmail(), bannedPerson.getEmail());
				
				mailManager.send(Configuration.getNoReplySenderName(), 
						         Configuration.getNoReplyEmailAddress(),
						         to,
						         null, 
						         null,
						         Configuration.getSiteName() + ": someone unbanned you",
						         objects, 
						         velocityTemplateLocation);
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
