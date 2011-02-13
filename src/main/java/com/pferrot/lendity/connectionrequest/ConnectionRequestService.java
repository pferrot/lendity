package com.pferrot.lendity.connectionrequest;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.ConnectionRequestDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.hibernate.utils.HibernateUtils;
import com.pferrot.lendity.model.ConnectionRequest;
import com.pferrot.lendity.model.ConnectionRequestResponse;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.security.SecurityUtils;

public class ConnectionRequestService {

	private final static Log log = LogFactory.getLog(ConnectionRequestService.class);
	
	private ConnectionRequestDao connectionRequestDao;
	private ListValueDao listValueDao;
	private PersonDao personDao;
	private MailManager mailManager;
	private PersonService personService;

	public void setMailManager(final MailManager pMailManager) {
		this.mailManager = pMailManager;
	}	
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	public void setConnectionRequestDao(final ConnectionRequestDao pConnectionRequestDao) {
		this.connectionRequestDao = pConnectionRequestDao;
	}
	
	public void setPersonDao(final PersonDao pPersonDao) {
		this.personDao = pPersonDao;
	}
	
	public void setListValueDao(final ListValueDao pListValueDao) {
		this.listValueDao = pListValueDao;
	}
	
	public ConnectionRequest findConnectionRequest(final Long pConnectionRequestId) {
		return connectionRequestDao.findConnectionRequest(pConnectionRequestId);
	}

	public ListWithRowCount findCurrentUserPendingConnectionRequests(final int pFirstResult, final int pMaxResults) {		
		return connectionRequestDao.findConnectionRequests(new Long[]{PersonUtils.getCurrentPersonId()}, null, null, null, null,
				Boolean.FALSE, null, null, "requestDate", Boolean.FALSE, pFirstResult, pMaxResults);
	}
	
	public long countCurrentUserPendingConnectionRequests() {
		return connectionRequestDao.countConnectionRequests(new Long[]{PersonUtils.getCurrentPersonId()}, null, null, null, null,
				Boolean.FALSE, null, null);
	}
	
	public ListWithRowCount findCurrentUserPendingConnectionRequestsOut(final int pFirstResult, final int pMaxResults) {		
		return connectionRequestDao.findConnectionRequests(null, new Long[]{PersonUtils.getCurrentPersonId()}, null, null, null,
				Boolean.FALSE, null, null, "requestDate", Boolean.FALSE, pFirstResult, pMaxResults);
		
	}
	
	public ListWithRowCount findCurrentUserConnectionsUpdates(final int pFirstResult, final int pMaxResults) {
		final Long[] connectionsIds = personService.getCurrentPersonConnectionIds(null);
		// Make sure one do not show all connections updates for someone who has no friend.
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		final ListValue acceptLV = listValueDao.findListValue(ConnectionRequestResponse.ACCEPT_LABEL_CODE);
		
		return connectionRequestDao.findConnectionRequests(connectionsIds, connectionsIds, Boolean.TRUE, new Long[]{PersonUtils.getCurrentPersonId()},
				new Long[]{PersonUtils.getCurrentPersonId()}, Boolean.TRUE, new Long[]{acceptLV.getId()},  null,
				"responseDate", Boolean.FALSE, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findPersonConnectionsUpdatesSince(final Person pPerson, final Date pResponseDateMin) {
		final Long[] connectionsIds = personService.getPersonConnectionIds(pPerson, null);
		// Make sure one do not show all connections updates for someone who has no friend.
		if (connectionsIds == null || connectionsIds.length == 0) {
			return ListWithRowCount.emptyListWithRowCount();
		}
		final ListValue acceptLV = listValueDao.findListValue(ConnectionRequestResponse.ACCEPT_LABEL_CODE);
		
		return connectionRequestDao.findConnectionRequests(connectionsIds, connectionsIds, Boolean.TRUE, new Long[]{pPerson.getId()},
				new Long[]{pPerson.getId()}, Boolean.TRUE, new Long[]{acceptLV.getId()}, pResponseDateMin,
				"responseDate", Boolean.FALSE, 0, 5);		
	}

	/**
	 * Returns false if the requester is not allowed to ask the other user for connecting (for any reason),
	 * true otherwise.
	 * 
	 * @param pConnection
	 * @param pRequester
	 * @return
	 * @throws ConnectionRequestException
	 */
	public boolean isConnectionRequestAllowed(final Person pConnection, final Person pRequester) throws ConnectionRequestException {
		// Connection must be active applications user.
		if (! PersonUtils.isActiveApplicationUser(pConnection)) {
			if (log.isDebugEnabled()) {
				log.debug("Connection is not an active app user: " + pConnection);
			}
			return false;
		}
		
		// Requester must be active applications user.
		if (! PersonUtils.isActiveApplicationUser(pRequester)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester is not an active app user: " + pRequester);
			}
			return false;			
		}
		
		// Same connection as requester.
		if (pConnection.equals(pRequester)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester and connection are the same person: " + pConnection);
			}
			return false;
		}
		
		// Request is banned.
		// Avoid LazyInitializationException
		final Collection<Person> bannedPersons = personService.findBannedPersonsList(pConnection.getId(), null, 0, 0);
		if (bannedPersons.contains(pRequester)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester (" + pRequester + ") is banned by connection (" + pConnection + ") .");
			}			
			return false;
		}
		
		// Already a connection.
		// Avoid LazyInitializationException
		final Collection<Person> connections = personService.findConnectionsList(pRequester.getId(), null, 0, 0);
		if (connections.contains(pConnection)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester (" + pRequester + ") and connection (" + pConnection + ") are already connected.");
			}			
			return false;
		}

		// Already a connection request pending.
		if (isUncompletedConnectionRequestAvailable(pConnection, pRequester)) {
			if (log.isDebugEnabled()) {
				log.debug("Already a connection request pending between requester (" + pRequester + ") and connection (" + pConnection + ").");
			}	
			return false;
		}
		
		return true;			
	}

	/**
	 * Returns false if the current user is not allowed to ask the other user for connecting (for any reason),
	 * true otherwise.
	 *
	 * @param pConnection
	 * @return
	 * @throws ConnectionRequestException
	 */
	public boolean isConnectionRequestAllowedFromCurrentUser(final Person pConnection) throws ConnectionRequestException {
		return SecurityUtils.isLoggedIn() && isConnectionRequestAllowed(pConnection, getCurrentPerson());		
	}

	/**
	 * This operation will send an email to the connection informing him that
	 * the current user wants to add him as a connection.

	 * @param pConnection
	 * @param pRequester
	 * @return
	 * @throws ConnectionRequestException 
	 */
	public Long createConnectionRequest(final Person pConnection, final Person pRequester) throws ConnectionRequestException {
		try {
			if (! isConnectionRequestAllowed(pConnection, pRequester)) {
				throw new ConnectionRequestException("Connection request not allowed.");
			}

			final ConnectionRequest connectionRequest = new ConnectionRequest();
			connectionRequest.setConnection(pConnection);
			connectionRequest.setRequester(pRequester);
			connectionRequest.setRequestDate(new Date());
			
			if (isUncompletedConnectionRequestAvailable(pConnection, pRequester)) {
				throw new ConnectionRequestException("Connection request not allowed."); 
			}
			
			Long connectionRequestId = connectionRequestDao.createConnectionRequest(connectionRequest);
				
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("connectionFirstName", pConnection.getFirstName());
			objects.put("requesterFirstName", pRequester.getFirstName());
			objects.put("requesterLastName", pRequester.getLastName());
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("invitationsUrl", Configuration.getRootURL() + PagesURL.MY_PENDING_CONNECTION_REQUESTS_LIST);
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/connectionrequest/ask/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pConnection.getEmail(), pConnection.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			mailManager.send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": invitation à devenir ami",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
			
			return connectionRequestId;
		} 
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}
	}

	/**
	 * Returns true if a there is already an connection request pending between two individuals.
	 *
	 * @param pPerson1
	 * @param pPerson2
	 * @return
	 */
	public boolean isUncompletedConnectionRequestAvailable(final Person pPerson1, final Person pPerson2) {
		CoreUtils.assertNotNull(pPerson1);
		CoreUtils.assertNotNull(pPerson2);

		final Long person1Id = pPerson1.getId();
		final Long person2Id = pPerson2.getId();
		
		long nbHits = connectionRequestDao.countConnectionRequests(new Long[]{person1Id}, new Long[]{person2Id}, null, null, null, Boolean.FALSE, null, null);
		if (nbHits > 0) {
			return true;
		}
		
		nbHits = connectionRequestDao.countConnectionRequests(new Long[]{person2Id}, new Long[]{person1Id}, null, null, null, Boolean.FALSE, null, null);
		return nbHits > 0;
	}
	

	/**
	 * Create a connection request using the current user as requester.
	 *
	 * @param pConnection
	 * @return
	 * @throws ConnectionRequestException
	 */
	public Long createConnectionRequestFromCurrentUser(final Person pConnection) throws ConnectionRequestException {		
		return createConnectionRequest(pConnection, getCurrentPerson());
	}

	/**
	 * Create a connection request using the current user as requester.
	 *
	 * @param pConnectionId
	 * @return
	 * @throws ConnectionRequestException
	 */
	public Long createConnectionRequestFromCurrentUser(final Long pConnectionId) throws ConnectionRequestException {		
		return createConnectionRequestFromCurrentUser(personDao.findPerson(pConnectionId));
	}
	
	/**
	 * The connection is simply refused.
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequest
	 * @throws ConnectionRequestException
	 */
	public void updateRefuseConnectionRequest(final ConnectionRequest pConnectionRequest) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequest);
			
			setConnectionRequestResponse(pConnectionRequest, (ConnectionRequestResponse)listValueDao.findListValue(ConnectionRequestResponse.REFUSE_LABEL_CODE));
			
			sendResponseEmail(pConnectionRequest, Configuration.getSiteName() + ": demande d'ami refusée", "com/pferrot/lendity/emailtemplate/connectionrequest/refuse/fr");

			if (log.isInfoEnabled()) {
				log.info("'" + pConnectionRequest.getRequester() + "' is refused by '" + pConnectionRequest.getConnection() + "'.");
			}
		}
		catch (ConnectionRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}		
	}

	/**
	 * The connection is simply refused.
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequestId
	 * @throws ConnectionRequestException
	 */
	public void updateRefuseConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		CoreUtils.assertNotNull(pConnectionRequestId);
		final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);

		updateRefuseConnectionRequest(connectionRequest);
	}

	/**
	 * The connection is refused and the requester is banned by the connection.
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequest
	 * @throws ConnectionRequestException
	 */
	public void updateBanConnectionRequest(final ConnectionRequest pConnectionRequest) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequest);
			
			setConnectionRequestResponse(pConnectionRequest, (ConnectionRequestResponse)listValueDao.findListValue(ConnectionRequestResponse.BAN_LABEL_CODE));

			// If the user is banned, it cannot be a connection.
			if (pConnectionRequest.getConnection().getConnections().contains(pConnectionRequest.getRequester()) ||
				pConnectionRequest.getRequester().getConnections().contains(pConnectionRequest.getConnection())) {
				if (log.isWarnEnabled()) {
					log.warn("'" + pConnectionRequest.getRequester() + "' is a connection of '" + pConnectionRequest.getConnection() + "' before ban.");
				}				
				// Reverse link is updated with this.
				pConnectionRequest.getConnection().removeConnection(pConnectionRequest.getRequester());
			}
			
			// This test should not be necessary, but it can eventually avoid inserting redundant information.
			if (!pConnectionRequest.getConnection().getBannedPersons().contains(pConnectionRequest.getRequester())) {
				// Ban connection.
				pConnectionRequest.getConnection().addBannedPerson(pConnectionRequest.getRequester());
			} else {
				if (log.isWarnEnabled()) {
					log.warn("'" + pConnectionRequest.getRequester() + "' was already banned by '" + pConnectionRequest.getConnection() + "'.");
				}
			}
			
			sendResponseEmail(pConnectionRequest,
					Configuration.getSiteName() + ": demande d'ami refusée et exclusion",
					"com/pferrot/lendity/emailtemplate/connectionrequest/ban/fr");

			if (log.isInfoEnabled()) {
				log.info("'" + pConnectionRequest.getRequester() + "' is banned by '" + pConnectionRequest.getConnection() + "'.");
			}
		}
		catch (ConnectionRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}	
	}

	/**
	 * The connection is refused and the requester is banned by the connection.
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequestId
	 * @throws ConnectionRequestException
	 */
	public void updateBanConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		CoreUtils.assertNotNull(pConnectionRequestId);
		final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);

		updateBanConnectionRequest(connectionRequest);	
	}
	
	/**
	 * The connection is accepted, both the requester and the connection are now connection of each other. 
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequest
	 * @throws ConnectionRequestException
	 */
	public void updateAcceptConnectionRequest(final ConnectionRequest pConnectionRequest) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequest);
			HibernateUtils.evictQueryCacheRegion("query.connections");

			setConnectionRequestResponse(pConnectionRequest, (ConnectionRequestResponse)listValueDao.findListValue(ConnectionRequestResponse.ACCEPT_LABEL_CODE));

			// If the user is a connection, he cannot be a banned.
			if (pConnectionRequest.getConnection().getBannedPersons().contains(pConnectionRequest.getRequester())) {
				if (log.isWarnEnabled()) {
					log.warn("'" + pConnectionRequest.getRequester() + "' is banned by '" + pConnectionRequest.getConnection() + "' before being added as connection.");
				}				
				pConnectionRequest.getConnection().removeBannedPerson(pConnectionRequest.getRequester());
			}

			// Same for reverse link.
			if (pConnectionRequest.getRequester().getBannedPersons().contains(pConnectionRequest.getConnection())) {
				if (log.isWarnEnabled()) {
					log.warn("'" + pConnectionRequest.getConnection() + "' is banned by '" + pConnectionRequest.getRequester() + "' before being added as connection.");
				}
				pConnectionRequest.getRequester().removeBannedPerson(pConnectionRequest.getConnection());
			}

			// This test should not be necessary, but it can eventually avoid inserting redundant information.
			if (!pConnectionRequest.getRequester().getConnections().contains(pConnectionRequest.getConnection())) {
				// Add connection (will add on reverse link as well).
				pConnectionRequest.getRequester().addConnection(pConnectionRequest.getConnection());
			}
			
			sendResponseEmail(pConnectionRequest,
					Configuration.getSiteName() + ": demande d'ami acceptée",
					"com/pferrot/lendity/emailtemplate/connectionrequest/accept/fr");

			if (log.isInfoEnabled()) {
				log.info("'" + pConnectionRequest.getRequester() + "' is accepted by '" + pConnectionRequest.getConnection() + "'.");
			}
		}
		catch (ConnectionRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}			
	}

	/**
	 * The connection is accepted, both the requester and the connection are now connection of each other. 
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequestId
	 * @throws ConnectionRequestException
	 */
	public void updateAcceptConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		CoreUtils.assertNotNull(pConnectionRequestId);
		final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);

		updateAcceptConnectionRequest(connectionRequest);
	}
	
	private void setConnectionRequestResponse(final ConnectionRequest pConnectionRequest, final ConnectionRequestResponse pConnectionRequestResponse) throws ConnectionRequestException {
		CoreUtils.assertNotNull(pConnectionRequest);
		CoreUtils.assertNotNull(pConnectionRequestResponse);
		
		assertConnectionIsCurrentUser(pConnectionRequest);
		
//		final Person connection = pConnectionRequest.getConnection();
		if (pConnectionRequest.getResponse() != null) {
			throw new ConnectionRequestException("Connection request with ID '" + pConnectionRequest.getId().toString() + "' already has a response.");
		}
		pConnectionRequest.setResponse(pConnectionRequestResponse);
		pConnectionRequest.setResponseDate(new Date());
	}
	
	private void sendResponseEmail(final ConnectionRequest pConnectionRequest, final String pEmailSubject, final String pTemplateLocation) {
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("requesterFirstName", pConnectionRequest.getRequester().getFirstName());
		objects.put("connectionFirstName", pConnectionRequest.getConnection().getFirstName());
		objects.put("connectionLastName", pConnectionRequest.getConnection().getLastName());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(pConnectionRequest.getRequester().getEmail(), pConnectionRequest.getRequester().getEmail());
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
		
		mailManager.send(Configuration.getNoReplySenderName(), 
						 Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         pEmailSubject,
				         objects, 
				         pTemplateLocation,
				         inlineResources);			
	}
	
	private void assertRequesterIsCurrentUser(final ConnectionRequest pConnectionRequest) throws ConnectionRequestException {
		assertConnectionOrRequesterIsCurrentUser(pConnectionRequest, false);
	}
	
	private void assertConnectionIsCurrentUser(final ConnectionRequest pConnectionRequest) throws ConnectionRequestException {
		assertConnectionOrRequesterIsCurrentUser(pConnectionRequest, true);
	}
	
	private void assertConnectionOrRequesterIsCurrentUser(final ConnectionRequest pConnectionRequest, boolean pConnection) throws ConnectionRequestException {
		CoreUtils.assertNotNull(pConnectionRequest);
		Person person = pConnection?pConnectionRequest.getConnection():pConnectionRequest.getRequester();
		if (!person.getId().equals(PersonUtils.getCurrentPersonId())) {
			throw new ConnectionRequestException("Only the current user can execute that operation.");
		}
	}

	private Person getCurrentPerson() {
		return personDao.findPerson(PersonUtils.getCurrentPersonId());
	}
}
