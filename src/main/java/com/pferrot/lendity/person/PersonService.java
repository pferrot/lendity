package com.pferrot.lendity.person;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.DocumentDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.PersonDaoQueryBean;
import com.pferrot.lendity.dao.hibernate.utils.HibernateUtils;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Country;
import com.pferrot.lendity.model.Document;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.OrderedListValue;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PersonDetailsVisibility;
import com.pferrot.lendity.model.WallCommentsAddPermission;
import com.pferrot.lendity.model.WallCommentsVisibility;
import com.pferrot.lendity.person.exception.PersonException;
import com.pferrot.lendity.utils.HtmlUtils;
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
	 * Returns true if the display name is available or it is the one pPerson, false otherwise.
	 *
	 * @param pDisplayName
	 * @param pPerson
	 * @return
	 */
	public boolean isDisplayNameAvailable(final String pDisplayName, final Person pPerson) {
		CoreUtils.assertNotNullOrEmptyString(pDisplayName);
		CoreUtils.assertNotNull(pPerson);
		
		final Person person = personDao.findPersonFromDisplayName(pDisplayName);
		
		// It is the display name of the person passed in parameter.
		if (person != null && pPerson.equals(person)) {
			return true;
		}
		
		return person == null;
	}
	
	/**
	 * Returns true if the display name is available, false otherwise.
	 *
	 * @param pDisplayName
	 * @return
	 */
	public boolean isDisplayNameAvailable(final String pDisplayName) {
		CoreUtils.assertNotNullOrEmptyString(pDisplayName);
		
		final Person person = personDao.findPersonFromDisplayName(pDisplayName);
		
		return person == null;
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
		CoreUtils.assertNotNull(pEvaluation.getScore());
		
		if (pEvaluation.getScore().intValue() == 1) {
			pPerson.setNbEvalScore1(pPerson.getNbEvalScore1() + 1);
		}
		else if (pEvaluation.getScore().intValue() == 2) {
			pPerson.setNbEvalScore2(pPerson.getNbEvalScore2() + 1);
		}
		else {
			throw new RuntimeException("Unsupported score: " + pEvaluation.getScore());
		}
		updatePersonPrivileged(pPerson);
	}

	public void updatePerson(final Person pPerson) {
		assertCurrentUserAuthorizedToEdit(pPerson);
		personDao.updatePerson(pPerson);
		PersonUtils.updatePersonInSession(pPerson, JsfUtils.getHttpServletRequest());
	}
	
	public void updatePerson(final Person pPerson, final Long pDetailsVisibilityId, final Long pWallCommentsVisibilityId, final Long pWallCommentsAddPermissionId) {
		pPerson.setDetailsVisibility((PersonDetailsVisibility)listValueDao.findListValue(pDetailsVisibilityId));
		pPerson.setWallCommentsVisibility((WallCommentsVisibility)listValueDao.findListValue(pWallCommentsVisibilityId));
		pPerson.setWallCommentsAddPermission((WallCommentsAddPermission)listValueDao.findListValue(pWallCommentsAddPermissionId));
		updatePerson(pPerson);
	}
	
	public void updatePersonPicture(final Person pPerson, final Document pPicture, final Document pThumbnail) {
		assertCurrentUserAuthorizedToEdit(pPerson);
		if (pPicture != null) {
			pPicture.setPublik(Boolean.TRUE);
		}
		if (pThumbnail != null) {
			pThumbnail.setPublik(Boolean.TRUE);
		}
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

	/**
	 * Returns 5 random enables persons.
	 * 
	 * @return
	 */
	public List<Person> findRandomPersonsHomepage() {
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		
		queryBean.setEnabled(Boolean.TRUE);
		queryBean.setOrderBy("random");
		queryBean.setFirstResult(0);
		queryBean.setMaxResults(5);
		if (SecurityUtils.isLoggedIn()) {
			queryBean.setPersonToIgnoreId(PersonUtils.getCurrentPersonId());
		}
		
		return personDao.findPersonsList(queryBean);
	}

	/**
	 * Returns 5 random enabled persons in the area of pOriginLatitude/pOriginLongitude.
	 * First we look in the a distance of 2 kilometers to search for really close persons.
	 * If there is less that 5 persons, then we look up to 20 km, then up to 100 km and finally distance.
	 * 
	 * @param pOriginLatitude
	 * @param pOriginLongitude
	 * @return
	 */
	public List<Item> findRandomPersonsHomepage(final Double pOriginLatitude, final Double pOriginLongitude) {
		CoreUtils.assertNotNull(pOriginLatitude);
		CoreUtils.assertNotNull(pOriginLongitude);
		
		final int maxResults = 5;
		
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		
		queryBean.setEnabled(Boolean.TRUE);
		queryBean.setOrderBy("random");
		queryBean.setFirstResult(0);
		queryBean.setMaxResults(5);
		if (SecurityUtils.isLoggedIn()) {
			queryBean.setPersonToIgnoreId(PersonUtils.getCurrentPersonId());
		}
		
		// Try 2km, then 20, then 100, then unlimited.
		queryBean.setMaxDistanceKm(new Double(2));
		queryBean.setOriginLatitude(pOriginLatitude);
		queryBean.setOriginLongitude(pOriginLongitude);
		ListWithRowCount lwrc = personDao.findPersons(queryBean);
		
		// Try 20km.
		if (lwrc.getRowCount() < maxResults) {
			queryBean.setMaxDistanceKm(new Double(20));
			lwrc = personDao.findPersons(queryBean);
		}
		else {
			return lwrc.getList();
		}
		
		// Try 100km.
		if (lwrc.getRowCount() < maxResults) {
			queryBean.setMaxDistanceKm(new Double(100));
			lwrc = personDao.findPersons(queryBean);
		}
		else {
			return lwrc.getList();
		}
		
		// Unlimited distance.
		if (lwrc.getRowCount() < maxResults) {
			queryBean.setMaxDistanceKm(null);
			lwrc = personDao.findPersons(queryBean);
		}
		else {
			return lwrc.getList();
		}
		
		return lwrc.getList();
	}
		
	public ListWithRowCount findEnabledPersons(final String pSearchString, final Double pMaxDistance, final String pOrderByField, final Boolean pOrderByAsc,
			final int pFirstResult, final int pMaxResults) {
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		queryBean.setSearchString(pSearchString);
		queryBean.setConnectionLink(PersonDao.UNSPECIFIED_LINK);
		queryBean.setEnabled(Boolean.TRUE);
		queryBean.setEmailExactMatch(Boolean.TRUE);
		queryBean.setMaxDistanceKm(pMaxDistance);
		queryBean.setOrderBy(pOrderByField);
		queryBean.setOrderByAscending(pOrderByAsc);
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		
		Double originLatitude = PersonUtils.getCurrentPersonAddressHomeLatitude();;
		Double originLongitude = PersonUtils.getCurrentPersonAddressHomeLongitude();;
		queryBean.setOriginLatitude(originLatitude);
		queryBean.setOriginLongitude(originLongitude);
		
		if (pMaxDistance != null &&
			(originLatitude == null || originLongitude == null)) {
			throw new RuntimeException("Can only search by distance if geolocation is available.");
		}
		
		return personDao.findPersons(queryBean);
	}
	
	public Person findEnabledPersonByEmail(final String pEmail) {
		CoreUtils.assertNotNullOrEmptyString(pEmail);
		
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		queryBean.setEnabled(Boolean.TRUE);
		queryBean.setEmail(pEmail);
		
		final List<Person> list = personDao.findPersonsList(queryBean);
		if (list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			return list.get(0);
		}
		else {
			throw new RuntimeException("More than one person with email: " + pEmail);
		}
	}

	public ListWithRowCount findConnections(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		return personDao.findPersons(pPersonId, PersonDao.CONNECTIONS_LINK, pSearchString, Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, null, pFirstResult, pMaxResults);
	}
	
	public List<Person> findConnectionsList(final Long pPersonId, final String pSearchString, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId); 
		return personDao.findPersonsList(pPersonId, PersonDao.CONNECTIONS_LINK, pSearchString, Boolean.TRUE, Boolean.TRUE, null, null, null, null, null, null, pFirstResult, pMaxResults);
	}
	
	public long countConnections(final Long pPersonId, final String pSearchString) {
		CoreUtils.assertNotNull(pPersonId);
		final PersonDaoQueryBean queryBean = new PersonDaoQueryBean();
		queryBean.setPersonId(pPersonId);
		queryBean.setConnectionLink(PersonDao.CONNECTIONS_LINK);
		queryBean.setEnabled(Boolean.TRUE);
		queryBean.setSearchString(pSearchString);
		queryBean.setEmailExactMatch(Boolean.TRUE);
		return personDao.countPersons(queryBean);
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
	 * Returns an array containing the IDs of all persons who banned pPerson if
	 * null is passed as a parameter for pBannedById or of one person if the parameter is not null.
	 * 
	 * @param pPerson
	 * @param pBannedById
	 * @return
	 */
	public Long[] getPersonBannedByIds(final Person pPerson, final Long pBannedById) {
		return getPersonIdsArray(pPerson, pBannedById, pPerson.getBannedByPersons());
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
		return getPersonIdsArray(pPerson, pConnectionId, pPerson.getConnections());
	}
	
	
	private static Long[] getPersonIdsArray(final Person pPerson, final Long pPersonId, final Set<Person> pPersons) {
		Long[] personsIds = null;
		// All connections
		if (pPersonId == null) {
			if (pPersons == null || pPersons.isEmpty()) {
				return new Long[]{Long.valueOf(-1)};
			}
			personsIds = new Long[pPersons.size()];
			int counter = 0;
			for(Person connection: pPersons) {			
				personsIds[counter] = connection.getId();
				counter++;
			}
		}
		// Only one connection - make sure that it is a connection of the user. If not, it is someone trying to hack...
		else {
			boolean connectionFound = false;
			if (pPersons != null) {
				for(Person connection: pPersons) {			
					if (pPersonId.equals(connection.getId())) {
						connectionFound = true;
						break;
					}
				}
			}
			if (!connectionFound) {
				throw new SecurityException("Person with ID " + pPerson.getId() + " is not a linked to person with ID " + pPersonId + " (current person: " + PersonUtils.getCurrentPersonId() + ").");
			}
			personsIds = new Long[]{pPersonId};
		}
		return personsIds;
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
			
			HibernateUtils.evictQueryCacheRegion("query.connections");
			
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
	 * Returns true if pBannedId is banned by pBannerId, false otherwise.
	 *
	 * @param pBannerId
	 * @param pBannedId
	 * @return
	 */
	public boolean isBannedBy(final Long pBannerId, final Long pBannedId) {
		CoreUtils.assertNotNull(pBannerId);
		CoreUtils.assertNotNull(pBannedId);
		
		final Person banner = findPerson(pBannerId);
		final Person banned = findPerson(pBannedId);
		
		return isBannedBy(banner, banned);
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

	/**
	 * Returns true if the pBanned is banned by pBanner.
	 *
	 * @param pBanner
	 * @param pBanned
	 * @return
	 */
	public boolean isBannedBy(final Person pBanner, final Person pBanned) {
		CoreUtils.assertNotNull(pBanner);
		CoreUtils.assertNotNull(pBanned);
		
		final Collection<Person> person1Banned = findBannedPersonsList(pBanner.getId(), null, 0, 0);
		
		
		return person1Banned.contains(pBanned);		
	}

	public List<Person> getCurrentPersonEnabledConnections() {
		return findConnectionsList(PersonUtils.getCurrentPersonId(), null, 0, 0);
	}

		
	public boolean isCurrentUserAuthorizedToViewDetails(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		
		final String personDetailsVisibilityCode = pPerson.getDetailsVisibility().getLabelCode(); 
		
		if (PersonDetailsVisibility.PUBLIC.equals(personDetailsVisibilityCode)) {
			return true;
		}
		else if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		else if (isCurrentUserAuthorizedToEdit(pPerson)) {
			return true;
		}
		else if (PersonDetailsVisibility.CONNECTIONS.equals(personDetailsVisibilityCode)) {
			return pPerson.getConnections().contains(getCurrentPerson());
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

	public List<OrderedListValue> getDetailsVisibilities() {
		return listValueDao.findOrderedListValue(PersonDetailsVisibility.class);
	}
	
	public List<OrderedListValue> getWallCommentsVisibilities() {
		return listValueDao.findOrderedListValue(WallCommentsVisibility.class);
	}
	
	public List<OrderedListValue> getWallCommentsAddPermissions() {
		return listValueDao.findOrderedListValue(WallCommentsAddPermission.class);
	}

	/**
	 * Replaces all occurrences of strings like {p123} with an href link to the
	 * corresponding object, e.g.:
	 * 
	 * <a href="http://www.lendity.ch/person/personOverview.faces?personID=123" target="_blank">The person title</a>
	 * 
	 * If pPerson is not authorized to view the person, a standard error text is used instead.
	 * 
	 * @param pText
	 * @param pPerson
	 * @return
	 */
	public String processPersonHref(final String pText, final Person pPerson) {
		return processPersonHref(pText, pPerson, true);
	}
	
	public String processPersonNoHref(final String pText, final Person pPerson) {
		return processPersonHref(pText, pPerson, false);
	}
	
	private String processPersonHref(final String pText, final Person pPerson, final boolean pWithHref) {
		if (StringUtils.isNullOrEmpty(pText)) {
			return pText;
		}
		
		final String regex = "\\{p[0-9]+\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(pText);

		final StringBuffer result = new StringBuffer();
		while (m.find()) {
			try {
				final String text = m.group();
				final Long personId = Long.parseLong(text.substring(2, text.length() - 1));
				final Person person = findPerson(personId);
				if (pWithHref) {
					m.appendReplacement(result, getHrefLinkToPerson(person, true));
				}
				else {
					m.appendReplacement(result, person.getDisplayName());
				}
			}
			catch (Exception e) {
				final Locale locale = I18nUtils.getDefaultLocale();
				final String s = I18nUtils.getMessageResourceString("comment_replacementError", locale);
				m.appendReplacement(result, s);	
			}
			
			
		}
		m.appendTail(result);	
		return result.toString();
	}
	
	private String getHrefLinkToPerson(final Person pPerson, final boolean pOpenInNewWindow) {
		return "<a href=\"" + 
			JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, pPerson.getId().toString()) +
			"\"" +
			(pOpenInNewWindow?" target=\"_blank\"":"") +
			">" + 
			HtmlUtils.escapeHtmlAndReplaceCr(pPerson.getDisplayName()) + 
			"</a>";
	}
}
