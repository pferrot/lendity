package com.pferrot.sharedcalendar.connectionrequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.Consts;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.security.SecurityUtils;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.sharedcalendar.dao.ConnectionRequestDao;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.ConnectionRequest;
import com.pferrot.sharedcalendar.model.ConnectionRequestResponse;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class ConnectionRequestService {

	private final static Log log = LogFactory.getLog(ConnectionRequestService.class);
	
	private ConnectionRequestDao connectionRequestDao;
	private ListValueDao listValueDao;
	private PersonDao personDao;
	private MailManager mailManager;

	public void setMailManager(final MailManager pMailManager) {
		this.mailManager = pMailManager;
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
		if (pConnection.getId().equals(pRequester.getId())) {
			if (log.isDebugEnabled()) {
				log.debug("Requester and connection are the same person: " + pConnection);
			}
			return false;
		}
		
		// Request is banned.
		if (pConnection.getBannedPersons().contains(pRequester)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester (" + pRequester + ") is banned by connection (" + pConnection + ") .");
			}			
			return false;
		}
		
		// Already a connection.
		if (pRequester.getConnections().contains(pConnection)) {
			if (log.isDebugEnabled()) {
				log.debug("Requester (" + pRequester + ") and connection (" + pConnection + ") are already connected.");
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
		final User currentUser = SecurityUtils.getCurrentUser();
		CoreUtils.assertNotNull(currentUser);
		final Person currentUserPerson = personDao.findPersonFromUser(currentUser);
		CoreUtils.assertNotNull(currentUserPerson);
		
		return isConnectionRequestAllowed(pConnection, currentUserPerson);		
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
			
			assertRequesterIsCurrentUser(connectionRequest);
			
			Long connectionRequestId = connectionRequestDao.createConnectionRequest(connectionRequest);
				
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("connectionFirstName", pConnection.getFirstName());
			objects.put("requesterFirstName", pRequester.getFirstName());
			objects.put("requesterLastName", pRequester.getLastName());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/sharedcalendar/emailtemplate/connectionrequest/ask/en";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pConnection.getEmail(), pConnection.getEmail());
			
			mailManager.send(Consts.DEFAULT_SENDER_NAME, 
					         Consts.DEFAULT_SENDER_ADDRESS,
					         to,
					         null, 
					         null,
					         "sharedcalendar.com: someone wants to enter your network",
					         objects, 
					         velocityTemplateLocation);		
			
			return connectionRequestId;
		} 
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}
	}
	
	public Long createConnectionRequestFromCurrentUser(final Person pConnection) throws ConnectionRequestException {
		final User currentUser = SecurityUtils.getCurrentUser();
		CoreUtils.assertNotNull(currentUser);
		final Person currentUserPerson = personDao.findPersonFromUser(currentUser);
		CoreUtils.assertNotNull(currentUserPerson);
		
		return createConnectionRequest(pConnection, currentUserPerson);
	}
	
	/**
	 * The connection is simply refused.
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequestId
	 * @throws ConnectionRequestException
	 */
	public void refuseConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequestId);
			final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);
			
			setConnectionRequestResponse(connectionRequest, (ConnectionRequestResponse)listValueDao.findListValue(ConnectionRequestResponse.REFUSE_LABEL_CODE));
			
			sendResponseEmail(connectionRequest, "sharedcalendar.com: your connection request has been rejected", "com/pferrot/sharedcalendar/emailtemplate/connectionrequest/refuse/en");
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
	public void banConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequestId);
			final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);
			
			setConnectionRequestResponse(connectionRequest, (ConnectionRequestResponse)listValueDao.findListValue(ConnectionRequestResponse.BAN_LABEL_CODE));
			
			// Ban connection.
			connectionRequest.getConnection().addBannedPerson(connectionRequest.getRequester());
			
			sendResponseEmail(connectionRequest,
					"sharedcalendar.com: your connection request has been rejected and you were banned",
					"com/pferrot/sharedcalendar/emailtemplate/connectionrequest/ban/en");
		}
		catch (ConnectionRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}		
	}
	
	/**
	 * The connection is accepted, both the requester and the connection are not connection of each other. 
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pConnectionRequestId
	 * @throws ConnectionRequestException
	 */
	public void acceptConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequestId);
			final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);

			setConnectionRequestResponse(connectionRequest, (ConnectionRequestResponse)listValueDao.findListValue(ConnectionRequestResponse.ACCEPT_LABEL_CODE));
			
			// Add connection.
			connectionRequest.getRequester().addConnection(connectionRequest.getConnection());
			
			sendResponseEmail(connectionRequest,
					"sharedcalendar.com: your connection request has been accepted",
					"com/pferrot/sharedcalendar/emailtemplate/connectionrequest/accept/en");
		}
		catch (ConnectionRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}			
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
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(pConnectionRequest.getRequester().getEmail(), pConnectionRequest.getRequester().getEmail());
		
		mailManager.send(Consts.DEFAULT_SENDER_NAME, 
				         Consts.DEFAULT_SENDER_ADDRESS,
				         to,
				         null, 
				         null,
				         pEmailSubject,
				         objects, 
				         pTemplateLocation);			
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
		CoreUtils.assertNotNull(person);		
		final User currentUser = SecurityUtils.getCurrentUser();
		CoreUtils.assertNotNull(currentUser);
		final Person currentUserPerson = personDao.findPersonFromUser(currentUser);
		CoreUtils.assertNotNull(currentUserPerson);
		if (person.getId().longValue() != currentUserPerson.getId().longValue()) {
			throw new ConnectionRequestException("Only the current user can execute that operation.");
		}
	}
}
