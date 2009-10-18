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
	 * This operation will send an email to the connection informing him that
	 * the current user wants to add him as a connection.

	 * @param pConnection
	 * @param pRequester
	 * @return
	 * @throws ConnectionRequestException 
	 */
	public Long createConnectionRequest(final Person pConnection, final Person pRequester) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnection);
			// Only users can be connections.
			CoreUtils.assertNotNull(pConnection.getUser());
			
			CoreUtils.assertNotNull(pRequester);
			// Only users can request.
			CoreUtils.assertNotNull(pRequester.getUser());
			
			final ConnectionRequest connectionRequest = new ConnectionRequest();
			connectionRequest.setConnection(pConnection);
			connectionRequest.setRequester(pRequester);
			connectionRequest.setRequestDate(new Date());
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
	
	public void denyConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequestId);
			final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);
			assertConnectionIsCurrentUser(connectionRequest);
			
			final Person connection = connectionRequest.getConnection();
			if (connectionRequest.getResponse() != null) {
				throw new ConnectionRequestException("Connection request with ID '" + pConnectionRequestId.toString() + "' already has a response.");
			}
			connectionRequest.setResponse((ConnectionRequestResponse)listValueDao.findListValue(ConnectionRequestResponse.REFUSE_LABEL_CODE));
			connectionRequest.setResponseDate(new Date());

			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("requesterFirstName", connectionRequest.getRequester().getFirstName());
			objects.put("connectionFirstName", connectionRequest.getConnection().getFirstName());
			objects.put("connectionLastName", connectionRequest.getConnection().getLastName());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/sharedcalendar/emailtemplate/connectionrequest/deny/en";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(connectionRequest.getRequester().getEmail(), connectionRequest.getRequester().getEmail());
			
			mailManager.send(Consts.DEFAULT_SENDER_NAME, 
					         Consts.DEFAULT_SENDER_ADDRESS,
					         to,
					         null, 
					         null,
					         "sharedcalendar.com: your connection request was rejected",
					         objects, 
					         velocityTemplateLocation);		
		}
		catch (ConnectionRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}		
	}
	
	public void acceptConnectionRequest(final Long pConnectionRequestId) throws ConnectionRequestException {
		try {
			CoreUtils.assertNotNull(pConnectionRequestId);
			final ConnectionRequest connectionRequest = connectionRequestDao.findConnectionRequest(pConnectionRequestId);
			assertConnectionIsCurrentUser(connectionRequest);
			// TODO
		}
		catch (ConnectionRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ConnectionRequestException(e);
		}			
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
