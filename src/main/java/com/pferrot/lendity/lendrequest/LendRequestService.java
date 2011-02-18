package com.pferrot.lendity.lendrequest;

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
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.LendRequestDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.lendrequest.exception.LendRequestException;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.LendRequest;
import com.pferrot.lendity.model.LendRequestResponse;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.SecurityUtils;

public class LendRequestService {

	private final static Log log = LogFactory.getLog(LendRequestService.class);
	
	private LendRequestDao lendRequestDao;
	private ListValueDao listValueDao;
	private PersonDao personDao;
	private ItemDao itemDao;
	private MailManager mailManager;
	private PersonService personService;
	private LendTransactionService lendTransactionService;

	public void setMailManager(final MailManager pMailManager) {
		this.mailManager = pMailManager;
	}	
	
	public void setLendRequestDao(final LendRequestDao pLendRequestDao) {
		this.lendRequestDao = pLendRequestDao;
	}
	
	public void setPersonDao(final PersonDao pPersonDao) {
		this.personDao = pPersonDao;
	}
	
	public void setListValueDao(final ListValueDao pListValueDao) {
		this.listValueDao = pListValueDao;
	}
	
	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public LendRequest findLendRequest(final Long pLendRequestId) {
		return lendRequestDao.findLendRequest(pLendRequestId);
	}

	public ListWithRowCount findCurrentUserPendingLendRequests(final int pFirstResult, final int pMaxResults) {		
		return lendRequestDao.findLendRequests(null, PersonUtils.getCurrentPersonId(), null, Boolean.FALSE, pFirstResult, pMaxResults);
	}
	
	public long countCurrentUserPendingLendRequests() {		
		return lendRequestDao.countLendRequests(null, PersonUtils.getCurrentPersonId(), null, Boolean.FALSE);
	}
	
	public ListWithRowCount findCurrentUserPendingLendRequestsOut(final int pFirstResult, final int pMaxResults) {		
		return lendRequestDao.findLendRequests(PersonUtils.getCurrentPersonId(), null, null, Boolean.FALSE, pFirstResult, pMaxResults);
	}

	/**
	 * Returns false if the requester is not allowed to ask the other user for connecting (for any reason),
	 * true otherwise.
	 * 
	 * @param pRequester
	 * @param pItem
	 * @return
	 */
	public boolean isLendRequestAllowed(final Person pRequester, final InternalItem pItem) {
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		// Avoid LazyInitializationException
		Collection<Person> connections = personService.findConnectionsList(pItem.getOwner().getId(), null, 0, 0);
		return pItem.isAvailable() && 
			connections.contains(pRequester) &&
			!isUncompletedLendRequestAvailable(pRequester, pItem);		
	}

	public boolean isLendRequestAllowedFromCurrentUser(final InternalItem pItem) {
		return isLendRequestAllowed(getCurrentPerson(), pItem);		
	}

	/**
	 * This operation will send an email to the owner of the requested item
	 * informing him that the current user would like to borrow the item.

	 * @param pConnection
	 * @param pRequester
	 * @return
	 * @throws ConnectionRequestException 
	 */
	public Long createLendRequest(final Person pRequester, final InternalItem pItem,
			final Date pStartDate, final Date pEndDate) throws LendRequestException {
		try {
			if (! isLendRequestAllowed(pRequester, pItem)) {
				throw new ConnectionRequestException("Lend request not allowed.");
			}

			final LendRequest lendRequest = new LendRequest();
			lendRequest.setOwner(pItem.getOwner());
			lendRequest.setItem(pItem);
			lendRequest.setRequester(pRequester);
			lendRequest.setRequestDate(new Date());
			lendRequest.setStartDate(pStartDate);
			lendRequest.setEndDate(pEndDate);
			
			Long lendRequestId = lendRequestDao.createLendRequest(lendRequest);
			
			Long lendTransactionId = lendTransactionService.createLendTransaction(lendRequest);
			
			// To keep a kind of bi-directional link.
			lendRequest.setTransaction(lendTransactionService.findLendTransaction(lendTransactionId));
			lendRequestDao.updateLendRequest(lendRequest);
				
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("ownerFirstName", pItem.getOwner().getFirstName());
			objects.put("ownerLastName", pItem.getOwner().getLastName());
			objects.put("ownerDisplayName", pItem.getOwner().getDisplayName());
			objects.put("requesterFirstName", pRequester.getFirstName());
			objects.put("requesterLastName", pRequester.getLastName());
			objects.put("requesterDisplayName", pRequester.getDisplayName());
			objects.put("itemTitle", pItem.getTitle());
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("pendingRequestsUrl", Configuration.getRootURL() + PagesURL.MY_PENDING_LEND_REQUESTS_LIST);
			objects.put("lendTransactionUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.LEND_TRANSACTION_OVERVIEW,
					PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_NEED_ID,
					lendTransactionId.toString()));
			
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lendrequest/ask/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pItem.getOwner().getEmail(), pItem.getOwner().getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			mailManager.send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": demande d'emprunt",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
			
			return lendRequestId;
		} 
		catch (Exception e) {
			throw new LendRequestException(e);
		}
	}
	
	public Long createLendRequestFromCurrentUser(final InternalItem pItem) throws LendRequestException {
		return createLendRequest(getCurrentPerson(), pItem, null, null);		
	}

	public Long createLendRequestFromCurrentUser(final Long pItemId) throws LendRequestException {
		return createLendRequestFromCurrentUser(itemDao.findInternalItem(pItemId));		
	}

	/**
	 * The request is accepted. 
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pLendRequest
	 * @throws LendRequestException
	 */
	public void updateAcceptLendRequest(final LendRequest pLendRequest) throws LendRequestException {
		try {
			CoreUtils.assertNotNull(pLendRequest);

			setLendRequestResponse(pLendRequest, (LendRequestResponse)listValueDao.findListValue(LendRequestResponse.ACCEPT_LABEL_CODE));
			
			sendResponseEmail(pLendRequest, Configuration.getSiteName() + ": demande d'emprunt acceptée",
					"com/pferrot/lendity/emailtemplate/lendrequest/accept/fr");

			if (log.isInfoEnabled()) {
				log.info("Lend request '" + pLendRequest.getId() + "' is accepted by '" + pLendRequest.getOwner() + "'.");
			}
		}
		catch (LendRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new LendRequestException(e);
		}			
	}

	/**
	 * The request is refused. 
	 * An email is send to the requester to let him know about that.
	 * 
	 * @param pLendRequest
	 * @throws LendRequestException
	 */
	public void updateRefuseLendRequest(final LendRequest pLendRequest) throws LendRequestException {
		try {
			CoreUtils.assertNotNull(pLendRequest);

			setLendRequestResponse(pLendRequest, (LendRequestResponse)listValueDao.findListValue(LendRequestResponse.REFUSE_LABEL_CODE));
			
			sendResponseEmail(pLendRequest, 
					Configuration.getSiteName() + ": demande d'emprunt refusée",
					"com/pferrot/lendity/emailtemplate/lendrequest/refuse/fr");

			if (log.isInfoEnabled()) {
				log.info("Lend request '" + pLendRequest.getId() + "' is refused by '" + pLendRequest.getOwner() + "'.");
			}
		}
		catch (LendRequestException e) {
			throw e;
		}
		catch (Exception e) {
			throw new LendRequestException(e);
		}			
	}

	private void sendResponseEmail(final LendRequest pLendRequest,
			final String pEmailSubject,
			final String pTemplateLocation) {
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("requesterFirstName", pLendRequest.getRequester().getFirstName());
		objects.put("requesterLastName", pLendRequest.getRequester().getLastName());
		objects.put("requesterDisplayName", pLendRequest.getRequester().getDisplayName());
		objects.put("ownerFirstName", pLendRequest.getOwner().getFirstName());
		objects.put("ownerLastName", pLendRequest.getOwner().getLastName());
		objects.put("ownerDisplayName", pLendRequest.getOwner().getDisplayName());
		objects.put("itemTitle", pLendRequest.getItem().getTitle());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		objects.put("lendTransactionUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
				PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_NEED_ID,
				pLendRequest.getTransaction().getId().toString()));	
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(pLendRequest.getRequester().getEmail(), pLendRequest.getRequester().getEmail());
		
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

	public void updateAcceptLendRequest(final Long pLendRequestId) throws LendRequestException {
		CoreUtils.assertNotNull(pLendRequestId);
		final LendRequest lendRequest = lendRequestDao.findLendRequest(pLendRequestId);

		updateAcceptLendRequest(lendRequest);
	}
	
	public void updateRefuseLendRequest(final Long pLendRequestId) throws LendRequestException {
		CoreUtils.assertNotNull(pLendRequestId);
		final LendRequest lendRequest = lendRequestDao.findLendRequest(pLendRequestId);

		updateRefuseLendRequest(lendRequest);
	}


	/**
	 * Returns true if a there is already an lend request pending for that item by that person.
	 *
	 * @param pItem
	 * @param pRequester
	 * @return
	 */
	public boolean isUncompletedLendRequestAvailable(final Person pRequester, final InternalItem pItem) {
		CoreUtils.assertNotNull(pItem);
		CoreUtils.assertNotNull(pRequester);

		return lendRequestDao.countLendRequests(pRequester.getId(), null, pItem.getId(), Boolean.FALSE) > 0;
	}
	
	private void assertOwnerIsCurrentUser(final LendRequest pLendRequest) throws LendRequestException {
		CoreUtils.assertNotNull(pLendRequest);
		Person person = pLendRequest.getOwner();
		if (!person.getId().equals(PersonUtils.getCurrentPersonId())) {
			throw new LendRequestException("Only the current user can execute that operation.");
		}
	}	
	
	private void setLendRequestResponse(final LendRequest pLendRequest, final LendRequestResponse pLendRequestResponse) throws LendRequestException {
		CoreUtils.assertNotNull(pLendRequest);
		CoreUtils.assertNotNull(pLendRequestResponse);
		
		assertOwnerIsCurrentUser(pLendRequest);
		
//		final Person connection = pConnectionRequest.getConnection();
		if (pLendRequest.getResponse() != null) {
			throw new LendRequestException("Lend request with ID '" + pLendRequest.getId().toString() + "' already has a response.");
		}
		pLendRequest.setResponse(pLendRequestResponse);
		pLendRequest.setResponseDate(new Date());
	}

	private Person getCurrentPerson() {
		return personService.getCurrentPerson();
	}
}
