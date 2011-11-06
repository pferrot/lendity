package com.pferrot.lendity.potentialconnection;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.dao.PotentialConnectionDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.PotentialConnectionDaoQueryBean;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PotentialConnection;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class PotentialConnectionService {

	private final static Log log = LogFactory.getLog(PotentialConnectionService.class);
	
	private PersonService personService;
	private ConnectionRequestService connectionRequestService;
	private PotentialConnectionDao potentialConnectionDao;
	private MailManager mailManager;

	public PotentialConnectionDao getPotentialConnectionDao() {
		return potentialConnectionDao;
	}

	public void setPotentialConnectionDao(
			PotentialConnectionDao potentialConnectionDao) {
		this.potentialConnectionDao = potentialConnectionDao;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public MailManager getMailManager() {
		return mailManager;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public Long createPotentialConnection(final PotentialConnection pPotentialConnection) {
		return potentialConnectionDao.createPotentialConnection(pPotentialConnection);
	}
	
	/**
	 * Create a PotentialConnection if and only if there is not one for the same person
	 * and same email already.
	 * 
	 * @param pPotentialConnection
	 * @return
	 */
	public Long createPotentialConnectionIfNotExists(final PotentialConnection pPotentialConnection) {
		final PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
		queryBean.setPersonId(pPotentialConnection.getPerson().getId());
		queryBean.setEmail(pPotentialConnection.getEmail());
		final long nbFound = potentialConnectionDao.countPotentialConnections(queryBean);
		if (nbFound == 0) {
			return createPotentialConnection(pPotentialConnection);
		}
		else {
			return null;
		}
	}
	
	private PotentialConnection refreshPotentialConnection(final PotentialConnection pPotentialConnection) {
		CoreUtils.assertNotNull(pPotentialConnection);
		
		if (pPotentialConnection.getPersonId() != null) {
			pPotentialConnection.setPerson(getPersonService().findPerson(pPotentialConnection.getPersonId()));
		}
		if (pPotentialConnection.getConnectionId() != null) {
			pPotentialConnection.setConnection(getPersonService().findPerson(pPotentialConnection.getConnectionId()));
		}
		
		return pPotentialConnection;
	}
	
	public Long createOrUpdatePotentialConnection(final PotentialConnection pPcToCreate) throws PotentialConnectionException {
		final PotentialConnection refreshedPC = refreshPotentialConnection(pPcToCreate);
		
		PotentialConnection existingPC = null;
		if (pPcToCreate.getEmail() != null) {
			existingPC = findPotentialConnection(refreshedPC.getPerson().getId(), pPcToCreate.getEmail());	
		}
		else if (pPcToCreate.getName() != null) {
			existingPC = findPotentialConnectionByName(refreshedPC.getPerson().getId(), pPcToCreate.getName());
		}
		else {
			throw new PotentialConnectionException("Both email and name are null");
		}
		
		
		if (existingPC == null) {
			return createPotentialConnection(refreshedPC);
		}
		else {
			// Remember the date of the latest invitation.
			if (refreshedPC.getInvitationSentOn() != null) {
				existingPC.setInvitationSentOn(refreshedPC.getInvitationSentOn());
			}
			if (PotentialConnection.SOURCE_INVITATION.equals(refreshedPC.getSource()) &&
				!PotentialConnection.SOURCE_INVITATION.equals(existingPC.getSource())) {
				// Invitation is the source to remember by preference so that "reverse invitations"
				// suggestions can be creates.
				existingPC.setSource(PotentialConnection.SOURCE_INVITATION);
			}
			if (refreshedPC.getConnection() != null && existingPC.getConnection() == null) {
				existingPC.setConnection(refreshedPC.getConnection());
			}
			if (refreshedPC.getDateFound() != null && existingPC.getDateFound() == null) {
				existingPC.setDateFound(refreshedPC.getDateFound());
			}
			if (!StringUtils.isNullOrEmpty(refreshedPC.getName()) && StringUtils.isNullOrEmpty(existingPC.getName())) {
				existingPC.setName(refreshedPC.getName());
			}
			updatePotentialConnection(existingPC);
			return existingPC.getId();
		}
	}
	
	public PotentialConnection findPotentialConnection(final Long pPersonId, final String pEmail) {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNullOrEmptyString(pEmail);
		
		final PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
		queryBean.setPersonId(pPersonId);
		queryBean.setEmail(pEmail);
		final List<PotentialConnection> list = potentialConnectionDao.findPotentialConnectionsList(queryBean);
		if (list == null || list.isEmpty()) {
			return null;
		}
		else if (list.size() == 1) {
			return list.get(0);
		}
		else {
			throw new RuntimeException("More than 1 PotentialConnecton for personId '" +
					pPersonId.toString() + "' and email '" + pEmail + "'");
		}
	}
	
	/**
	 * Note that there can be several potential connections with the same name. In that case, the first
	 * match is returned.
	 * 
	 * @param pPersonId
	 * @param pName
	 * @return
	 */
	public PotentialConnection findPotentialConnectionByName(final Long pPersonId, final String pName) {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNullOrEmptyString(pName);
		
		final PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
		queryBean.setPersonId(pPersonId);
		queryBean.setName(pName);
		final List<PotentialConnection> list = potentialConnectionDao.findPotentialConnectionsList(queryBean);
		if (list == null || list.isEmpty()) {
			return null;
		}
		else {
			return list.get(0);
		}
	}
	
	public PotentialConnection findPotentialConnection(final Long pPotentialConnectionId) {
		return potentialConnectionDao.findPotentialConnection(pPotentialConnectionId);
	}
	
	public ListWithRowCount findPotentialConnections(final PotentialConnectionDaoQueryBean pQueryBean) {
		return potentialConnectionDao.findPotentialConnections(pQueryBean);
	}
	
	public List<PotentialConnection> findPotentialConnectionsList(final PotentialConnectionDaoQueryBean pQueryBean) {
		return potentialConnectionDao.findPotentialConnectionsList(pQueryBean);
	}
	
	public void updatePotentialConnection(final PotentialConnection pPotentialConnection) {
		potentialConnectionDao.updatePotentialConnection(pPotentialConnection);
	}
	
	public void deletePotentialConnection(final PotentialConnection pPotentialConnection) {
		potentialConnectionDao.deletePotentialConnection(pPotentialConnection);
	}
	
	public void deletePotentialConnectionsForPerson(final Long pPersonId) {
		potentialConnectionDao.deletePotentialConnectionsForPerson(pPersonId);
	}
	
	public void deletePotentialConnectionsForPersonAndConnection(final Long pPersonId, final Long pConnectionId) {
		potentialConnectionDao.deletePotentialConnectionForPersonAndConnection(pPersonId, pConnectionId);
	}
	
	public long countPotentialConnections(final PotentialConnectionDaoQueryBean pQueryBean) {
		return potentialConnectionDao.countPotentialConnections(pQueryBean);
	}

	/**
	 * Returns unignored potential connections for the current user.
	 *
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	public ListWithRowCount findCurrentPersonPotentialConnectons(final int pFirstResult, final int pMaxResults) {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("User not logged in.");
		}
		final PotentialConnectionDaoQueryBean queryBean = getCurrentPersonPotentialConnectionsQueryBean(pFirstResult, pMaxResults);
		
		return getPotentialConnectionDao().findPotentialConnections(queryBean);
	}
	
	public List<PotentialConnection> findCurrentPersonPotentialConnectonsList(final int pFirstResult, final int pMaxResults) {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("User not logged in.");
		}
		final PotentialConnectionDaoQueryBean queryBean = getCurrentPersonPotentialConnectionsQueryBean(pFirstResult, pMaxResults);
		
		return getPotentialConnectionDao().findPotentialConnectionsList(queryBean);
	}
	
	private PotentialConnectionDaoQueryBean getCurrentPersonPotentialConnectionsQueryBean(final int pFirstResult, final int pMaxResults) {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("User not logged in.");
		}
		final Person currentPerson = getPersonService().getCurrentPerson();
		final Long[] personConnectionsIds = getPersonService().getPersonConnectionIds(currentPerson, null);
		final Long[] personBannedByIds = getPersonService().getPersonBannedByIds(currentPerson, null);
		final Long[] pendingConnectionRequestsConnectionsIds = getConnectionRequestService().getPersonPendingConnectionRequestsConnectionsIds(currentPerson);
		
		final PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
		queryBean.setPersonId(PersonUtils.getCurrentPersonId());
		queryBean.setEmail(null);
		queryBean.setName(null);
		queryBean.setSource(null);
		queryBean.setIgnored(Boolean.FALSE);
		queryBean.setConnectionExists(Boolean.TRUE);
		
		queryBean.setAlreadyConnected(Boolean.FALSE);
		queryBean.setPersonConnectionsIds(personConnectionsIds);
		
		queryBean.setConnectionRequestPending(Boolean.FALSE);
		queryBean.setPendingConnectionRequestConnectionsIds(pendingConnectionRequestsConnectionsIds);
		
		queryBean.setBannedByPerson(Boolean.FALSE);
		queryBean.setBannedByPersonsIds(personBannedByIds);
		
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		
		return queryBean;
	}
	
	

	public ListWithRowCount findCurrentPersonFullPotentialConnectons(final int pFirstResult, final int pMaxResults) {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("User not logged in.");
		}
		
		final PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
		queryBean.setPersonId(PersonUtils.getCurrentPersonId());
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		
		return getPotentialConnectionDao().findPotentialConnections(queryBean);
	}

	/**
	 * Notify the potential connections of person that he has joined lendity.
	 * Also updates the PotentialConnection to link it to the new Person.
	 *
	 * @param person
	 * @throws PotentialConnectionException 
	 */
	public void updateAndNotifyPotentialConnections(final Person pConnection) throws PotentialConnectionException {
		if (pConnection == null ||
			!pConnection.isEnabled()) {
			return;
		}
		try {
			// In order to avoir sending 2 emails to the same person.
			final Set<Long> recipients = new HashSet<Long>();
			
			// Search on email.
			PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
			queryBean.setEmail(pConnection.getEmail());
			queryBean.setConnectionExists(Boolean.FALSE);
			queryBean.setPersonEnabled(Boolean.TRUE);
			
			List<PotentialConnection> potentialConnections = findPotentialConnectionsList(queryBean);
			
			for (PotentialConnection pc: potentialConnections) {
				sendPotentialConnectionNotification(pConnection, pc, recipients);
			}

			// Search on name.
			queryBean = new PotentialConnectionDaoQueryBean();
			queryBean.setName(pConnection.getFirstName() + " " + pConnection.getLastName());
			queryBean.setConnectionExists(Boolean.FALSE);
			queryBean.setPersonEnabled(Boolean.TRUE);
			
			potentialConnections = findPotentialConnectionsList(queryBean);
			
			for (PotentialConnection pc: potentialConnections) {
				sendPotentialConnectionNotification(pConnection, pc, recipients);
			}
		} 
		catch (Exception e) {
			throw new PotentialConnectionException(e);
		}		
	}
	
	private void sendPotentialConnectionNotification(final Person pConnection,
			                                         final PotentialConnection pPotentialConnection,
			                                         final Set<Long> pAlreadyRecipientsIds) {
		final Person person = pPotentialConnection.getPerson();
		
		pPotentialConnection.setConnection(pConnection);
		pPotentialConnection.setDateFound(new Date());
		updatePotentialConnection(pPotentialConnection);
		
		if (pAlreadyRecipientsIds.contains(person.getId())) {
			return;
		}
		else {
			pAlreadyRecipientsIds.add(person.getId());
		}
		
		// Do not notify if person do not want to receive those notifs or potential connection is ignored.
		if (!Boolean.TRUE.equals(person.getReceivePotentialConnectionNotif()) ||
			Boolean.TRUE.equals(pPotentialConnection.getIgnored())) {
			return;
		}
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("personFirstName", person.getFirstName());
		objects.put("personLastName", person.getLastName());
		objects.put("personDisplayName", person.getDisplayName());
		
		objects.put("connectionEmail", pPotentialConnection.getEmail());
		objects.put("connectionName", pPotentialConnection.getName());
		objects.put("connectionDisplayName", pConnection.getDisplayName());
		
		objects.put("connectionUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, pConnection.getId().toString()));
		
		objects.put("profileUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.MY_PROFILE));
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/potentialconnection/joined/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(person.getEmail(), person.getEmail());
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
		
		getMailManager().send(Configuration.getNoReplySenderName(), 
				         Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         Configuration.getSiteName() + ": un ami potentiel a rejoint Lendity",
				         objects, 
				         velocityTemplateLocation,
				         inlineResources);					
	}
	
	
	/**
	 * Create potential connections based on persons who invited pConnection. Indeed, if some other user invited
	 * him, then it is a potential connection.
	 * 
	 * @param pConnection
	 */
	public void createReverseInvitationPotentialConnections(final Person pConnection) {
		if (pConnection == null ||
			!pConnection.isEnabled()) {
			return;
		}	
		final PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
		queryBean.setEmail(pConnection.getEmail());
		queryBean.setPersonEnabled(Boolean.TRUE);
		queryBean.setSource(PotentialConnection.SOURCE_INVITATION);
		
		final List<PotentialConnection> potentialConnections = findPotentialConnectionsList(queryBean);
		final Date now = new Date();
		for (PotentialConnection pc: potentialConnections) {
			final Person person = pc.getPerson();
			
			final PotentialConnection reversePC = new PotentialConnection();
			reversePC.setConnection(person);
			reversePC.setEmail(person.getEmail());
			reversePC.setDateAdded(now);
			reversePC.setDateFound(now);
			reversePC.setIgnored(Boolean.FALSE);
			reversePC.setPerson(pConnection);
			reversePC.setSource(PotentialConnection.SOURCE_REVERSE_INVITATION);
			
			createPotentialConnection(reversePC);
		}	
	}

	public boolean isInvitationSentAlready(final Long pFromPersonId, final String pToEmail) {
		return getInvitationSentOnDate(pFromPersonId, pToEmail) != null;
	}

	public Date getInvitationSentOnDate(final Long pFromPersonId, final String pToEmail) {
		CoreUtils.assertNotNull(pFromPersonId);
		CoreUtils.assertNotNullOrEmptyString(pToEmail);
		
		final PotentialConnectionDaoQueryBean queryBean = new PotentialConnectionDaoQueryBean();
		queryBean.setPersonId(pFromPersonId);
		queryBean.setEmail(pToEmail);
		queryBean.setInvitationSent(Boolean.TRUE);
		
		final List<PotentialConnection> l = getPotentialConnectionDao().findPotentialConnectionsList(queryBean);
		if (l == null || l.isEmpty()) {
			return null;
		}
		else {
			final PotentialConnection pc = l.get(0);
			return pc.getInvitationSentOn();
		}
	}
}
