package com.pferrot.lendity.person;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.DocumentDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.hibernate.utils.HibernateUtils;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.model.Country;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.exception.PersonException;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class PersonService {

	private final static Log log = LogFactory.getLog(PersonService.class);
	
	private PersonDao personDao;
	private MailManager mailManager;
	private DocumentDao documentDao;
	private ListValueDao listValueDao;
	private DocumentService documentService;

	public void setPersonDao(final PersonDao pPersonDao) {
		this.personDao = pPersonDao;
	}
	
	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public Person findPerson(final Long pPersonId) {
		return personDao.findPerson(pPersonId);
	}

	public String findPersonDisplayName(final Long pPersonId) {
		return personDao.findPerson(pPersonId).getDisplayName();
	}
	
	public Long createPerson(final Person pPerson) {
		return personDao.createPerson(pPerson);
	}
	
	/**
	 * No access control check.
	 * @param pPerson
	 */
	public void updatePersonPrivileged(final Person pPerson) {
		personDao.updatePerson(pPerson);
	}

	public void updatePersonAddEvaluation(final Person pPerson, final Evaluation pEvaluation) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pEvaluation);
		
		Double temp = pPerson.getNbEvaluations() * pPerson.getEvaluationAverage();
		pPerson.setNbEvaluations(Integer.valueOf(pPerson.getNbEvaluations().intValue() + 1));
		temp = temp + pEvaluation.getScore();
		temp = temp / pPerson.getNbEvaluations();
		pPerson.setEvaluationAverage(temp);
		updatePersonPrivileged(pPerson);
	}

	public void updatePerson(final Person pPerson) {
		assertCurrentUserAuthorizedToEdit(pPerson);
		personDao.updatePerson(pPerson);
		PersonUtils.updatePersonInSession(pPerson, JsfUtils.getHttpServletRequest());
	}
	
	public void updatePersonPicture(final Person pPerson, final Document pPicture, final Document pThumbnail) {
		assertCurrentUserAuthorizedToEdit(pPerson);
		final Document oldPic = pPerson.getImage();
		final Document oldThumbnail = pPerson.getThumbnail();		
		if (pPicture != null) {
			documentDao.createDocument(pPicture);
		}
		pPerson.setImage(pPicture);
		if (pThumbnail != null) {
			documentDao.createDocument(pThumbnail);
		}
		pPerson.setThumbnail(pThumbnail);
		
		if (oldPic != null) {
			documentDao.deleteDocument(oldPic);
		}
		if (oldThumbnail != null) {
			documentDao.deleteDocument(oldThumbnail);
		}
		
		personDao.updatePerson(pPerson);
 	}
	
	public List<Person> findEmailSubscribers(final Date pEmailSubscriberLastUpdateMax, final int pMaxNbToFind) {
		return personDao.findPersonsList(null, PersonDao.UNSPECIFIED_LINK, null, null, Boolean.TRUE, null, Boolean.TRUE, pEmailSubscriberLastUpdateMax, null, null, null, 0, pMaxNbToFind);
	}
		
	public ListWithRowCount findEnabledPersons(final String pSearchString, final Double pMaxDistance, final int pFirstResult, final int pMaxResults) {
		Double originaLatitude = null;
		Double originaLongitude = null;
		if (pMaxDistance != null) {
			Person p = getCurrentPerson();
			originaLatitude = p.getAddressHomeLatitude();
			originaLongitude = p.getAddressHomeLongitude();
			if (originaLatitude == null || originaLongitude == null) {
				throw new RuntimeException("Can only search by distance if geolocation is available.");
			}
		}
		return personDao.findPersons(null, PersonDao.UNSPECIFIED_LINK, pSearchString, Boolean.TRUE, Boolean.TRUE, null, null, null, pMaxDistance, originaLatitude, originaLongitude, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findConnections(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		return personDao.findPersons(pPersonId, PersonDao.CONNECTIONS_LINK, pSearchString, Boolean.FALSE, Boolean.TRUE, null, null, null, null, null, null, pFirstResult, pMaxResults);
	}
	
	public List<Person> findConnectionsList(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId); 
		return personDao.findPersonsList(pPersonId, PersonDao.CONNECTIONS_LINK, pSearchString, Boolean.FALSE, Boolean.TRUE, null, null, null, null, null, null, pFirstResult, pMaxResults);
	}
	
	public List<Person> findConnectionsRecevingNeedsNotificationsList(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		return personDao.findPersonsList(pPersonId, PersonDao.CONNECTIONS_LINK, pSearchString, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, pFirstResult, pMaxResults);
	}
	
	public ListWithRowCount findBannedPersons(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		// It is correct to search on the BANNED_BY_PERSONS_LINK: one actually search for that where ban by the current user.
		return personDao.findPersons(pPersonId, PersonDao.BANNED_BY_PERSONS_LINK, pSearchString, Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, null, pFirstResult, pMaxResults);
	}
	
	public List<Person> findBannedPersonsList(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		// It is correct to search on the BANNED_BY_PERSONS_LINK: one actually search for that where ban by the current user.
		return personDao.findPersonsList(pPersonId, PersonDao.BANNED_BY_PERSONS_LINK, pSearchString, Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, null, pFirstResult, pMaxResults);
	}

	public Long[] getCurrentPersonConnectionIds(final Long pConnectionId) {
		return getPersonConnectionIds(getCurrentPerson(), pConnectionId);		
	}

	public String getProfilePictureSrc(final Person pPerson, final boolean pAuthorizeDocumentAccess) {
		final Document picture = pPerson.getImage();
		if (picture == null ) {
			return JsfUtils.getFullUrl(PersonConsts.DUMMY_PROFILE_PICTURE_URL);
		}
		else {
			if (pAuthorizeDocumentAccess) {
				documentService.authorizeDownloadOneMinute(JsfUtils.getSession(), picture.getId());
			}
			return JsfUtils.getFullUrl(
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					picture.getId().toString());
		}			
	}
	
	public String getProfileThumbnailSrc(final Person pPerson, final boolean pAuthorizeDocumentAccess) {
		return getProfileThumbnailSrc(pPerson, pAuthorizeDocumentAccess, JsfUtils.getSession(), JsfUtils.getContextRoot());		
	}
	
	public String getProfileThumbnailSrc(final Person pPerson, final boolean pAuthorizeDocumentAccess,
			final HttpSession pSession, final String pUrlPrefix) {
		final Document thumbnail = pPerson.getThumbnail();
		if (thumbnail == null ) {
			return JsfUtils.getFullUrlWithPrefix(pUrlPrefix, PersonConsts.DUMMY_PROFILE_THUMBNAIL_URL);
		}
		else {
			if (pAuthorizeDocumentAccess) {
				documentService.authorizeDownloadOneMinute(pSession, thumbnail.getId());
			}
			return JsfUtils.getFullUrlWithPrefix(
					pUrlPrefix,
					PagesURL.DOCUMENT_DOWNLOAD, 
					PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, 
					thumbnail.getId().toString());
		}		
	}
	
	/**
	 * Returns an array containing the IDs of all connections of the user if
	 * null is passed as a parameter or of one connection if the parameter is not null.
	 * 
	 * @param pPerson
	 * @param pConnectionId
	 * @return
	 */
	public Long[] getPersonConnectionIds(final Person pPerson, final Long pConnectionId) {
		Long[] connectionsIds = null;
		// All connections
		if (pConnectionId == null) {
			final Set<Person> connections = pPerson.getConnections();
			if (connections == null || connections.isEmpty()) {
				return new Long[]{Long.valueOf(-1)};
			}
			connectionsIds = new Long[connections.size()];
			int counter = 0;
			for(Person connection: connections) {			
				connectionsIds[counter] = connection.getId();
				counter++;
			}
		}
		// Only one connection - make sure that it is a connection of the user. If not, it is someone trying to hack...
		else {
			final Person person = getCurrentPerson();
			final Set<Person> connections = person.getConnections();
			boolean connectionFound = false;
			if (connections != null) {
				for(Person connection: connections) {			
					if (pConnectionId.equals(connection.getId())) {
						connectionFound = true;
						break;
					}
				}
			}
			if (!connectionFound) {
				throw new SecurityException("Person with ID '" + PersonUtils.getCurrentPersonId() + "' tried to display details about person with " +
						"ID '" + pConnectionId.toString() + "' but is not a connection.");
			}
			connectionsIds = new Long[]{pConnectionId};
		}
		return connectionsIds;
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
			
			HibernateUtils.evictQueryCacheRegion("query.connections");
			
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

	/**
	 * Returns true if the 2 persons are connections, false otherwise.
	 *
	 * @param pPerson1Id
	 * @param pPerson2Id
	 * @return
	 */
	public boolean isConnection(final Long pPerson1Id, final Long pPerson2Id) {
		CoreUtils.assertNotNull(pPerson1Id);
		CoreUtils.assertNotNull(pPerson2Id);
		
		final Person p1 = findPerson(pPerson1Id);
		final Person p2 = findPerson(pPerson2Id);
		
		return isConnection(p1, p2);
	}
	
	/**
	 * Returns true if the 2 persons are connections, false otherwise.
	 *
	 * @param pPerson1
	 * @param pPerson2
	 * @return
	 */
	public boolean isConnection(final Person pPerson1, final Person pPerson2) {
		CoreUtils.assertNotNull(pPerson1);
		CoreUtils.assertNotNull(pPerson2);
		
		final Collection<Person> person1Connections = findConnectionsList(pPerson1.getId(), null, 0, 0);
		
		
		return person1Connections.contains(pPerson2);		
	}

	public List<Person> getCurrentPersonEnabledConnections() {
		return findConnectionsList(PersonUtils.getCurrentPersonId(), null, 0, 0);
	}

		
	public boolean isCurrentUserAuthorizedToViewEmail(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		if (isCurrentUserAuthorizedToEdit(pPerson)) {
			return true;
		}
		// Connections can view.
		if (pPerson.getConnections().contains(getCurrentPerson())) {
			return true;
		}
		return false;
	}
	
	public boolean isCurrentUserAuthorizedToView(final Person pPerson) {
		return true;
	}

	public void assertCurrentUserAuthorizedToView(final Person pPerson) {
		if (!isCurrentUserAuthorizedToView(pPerson)) {
			throw new SecurityException("Current user is not authorized to view person");
		}
	}

	public boolean isCurrentUserAuthorizedToEdit(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
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
	

	/**
	 * Returns the current person for the logged in user or null
	 * if not logged in.
	 *
	 * @return
	 */
	public Person getCurrentPerson() {
		if (SecurityUtils.isLoggedIn()) {
			return personDao.findPerson(PersonUtils.getCurrentPersonId());
		}
		else {
			return null;
		}
	}

	public List<ListValue> getCountries() {
		return listValueDao.findListValue(Country.class);
	}	
}
