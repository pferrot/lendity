package com.pferrot.lendity.lendtransaction;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.bean.LendTransactionDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.exception.ItemException;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.LendRequest;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.OrderedListValue;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;
import com.pferrot.security.SecurityUtils;

public class LendTransactionService {

	private final static Log log = LogFactory.getLog(LendTransactionService.class);
	
	private LendTransactionDao lendTransactionDao;
	private ListValueDao listValueDao;
	private MailManager mailManager;
	private PersonService personService;
	private ItemService itemService;

	public void setMailManager(final MailManager pMailManager) {
		this.mailManager = pMailManager;
	}	
	
	public void setLendTransactionDao(final LendTransactionDao pLendTransactionDao) {
		this.lendTransactionDao = pLendTransactionDao;
	}
	
	public void setListValueDao(final ListValueDao pListValueDao) {
		this.listValueDao = pListValueDao;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public LendTransaction findLendTransaction(final Long pLendTransactionId) {
		return lendTransactionDao.findLendTransaction(pLendTransactionId);
	}
	
	public LendTransaction findInProgressLendTransactionForItem(final Item pItem) throws LendTransactionException {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setItemId(pItem.getId());
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final Long[] inProgressIds = new Long[]{inProgress.getId()};
		queryBean.setStatusIds(inProgressIds);
		final ListWithRowCount lwrc = lendTransactionDao.findLendTransactions(queryBean);
		if (lwrc.getRowCount() > 1) {
			throw new LendTransactionException("Should not have found more than 1 in progress lend transaction" +
					" for item = " + pItem.getId());
		}
		else if (lwrc.getRowCount() == 0) {
			return null;
		}
		else {
			return (LendTransaction)lwrc.getList().get(0);
		}
	}
	
	public ListWithRowCount findUncompletedLendTransactionForItemAndBorrower(final Long pItemId, final Long pBorrowerId,
			final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = getUncompletedLendTransactionForItemAndBorrowerQueryBean(pItemId, pBorrowerId, pFirstResult, pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}

	public ListWithRowCount findUncompletedLendTransactionForItemAndBorrower(final Item pItem, final Person pBorrower,
			final int pFirstResult, final int pMaxResults) {
		return findUncompletedLendTransactionForItemAndBorrower(pItem.getId(), pBorrower.getId(), pFirstResult, pMaxResults);
	}

	public long countUncompletedLendTransactionForItemAndBorrower(final Item pItem, final Person pBorrower) {
		final LendTransactionDaoQueryBean queryBean = getUncompletedLendTransactionForItemAndBorrowerQueryBean(pItem.getId(), pBorrower.getId(), 0, 0);
		return lendTransactionDao.countLendTransactions(queryBean);
	}
	
	private LendTransactionDaoQueryBean getUncompletedLendTransactionForItemAndBorrowerQueryBean(final Long pItemId, final Long pBorrowerId,
			final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setItemId(pItemId);
		final ListValue initialized = listValueDao.findListValue(LendTransactionStatus.INITIALIZED_LABEL_CODE);
		final ListValue opened = listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE);
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final Long[] ids = new Long[]{initialized.getId(), opened.getId(), inProgress.getId()};
		queryBean.setStatusIds(ids);
		queryBean.setBorrowerId(pBorrowerId);
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return queryBean;
	}

	public ListWithRowCount findUncompletedLendTransactionForItemAndLender(final Long pItemId, final Long pLenderId,
			final int pFirstResult, final int pMaxResults) {
		
		final LendTransactionDaoQueryBean queryBean = getUncompletedLendTransactionsForItemAndLenderQueryBean(pItemId, pLenderId, pFirstResult, pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}

	public long countUncompletedLendTransactionForItemAndLender(final Long pItemId, final Long pLenderId) {
		
		final LendTransactionDaoQueryBean queryBean = getUncompletedLendTransactionsForItemAndLenderQueryBean(pItemId, pLenderId, 0, 0);
		return lendTransactionDao.countLendTransactions(queryBean);
	}

	private LendTransactionDaoQueryBean getUncompletedLendTransactionsForItemAndLenderQueryBean(final Long pItemId, final Long pLenderId,
			final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		
		queryBean.setItemId(pItemId);
		queryBean.setStatusIds(getUncompletedStatusIds());
		
		queryBean.setLenderId(pLenderId);
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		
		return queryBean;
	}

	public ListWithRowCount findLendTransactionForItemAndBorrower(final Item pItem, final Person pBorrower, final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setItemId(pItem.getId());
		queryBean.setBorrowerId(pBorrower.getId());
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}

	public ListWithRowCount findLendTransactionsForItemAndPerson(final Item pItem, final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findLendTransactionsForItemAndPerson(pItem.getId(), pPerson.getId(), null, pFirstResult, pMaxResults);
	}

	public ListWithRowCount findLendTransactionsForItemAndPerson(final Long pItemId, final Long pPersonId, final Long pStatusId, final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = getLendTransactionsForItemAndPersonQueryBean(pItemId, pPersonId, pStatusId, pFirstResult, pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}
	
	public ListWithRowCount findLendTransactionsWaitingForInputForCurrentPerson(final int pFirstResult, final int pMaxResults) {
		if (!SecurityUtils.isLoggedIn()) {
			throw new SecurityException("Not logged in");
		}
		return findLendTransactionsWaitingForInput(PersonUtils.getCurrentPersonId(), pFirstResult, pMaxResults);
	}

	public ListWithRowCount findLendTransactionsWaitingForInput(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		return lendTransactionDao.findLendTransactionsWaitingForInput(pPersonId, pFirstResult, pMaxResults);
	}
	
	/**
	 * Returns lend transactions in status INITIALIZED or OPENED that have their start
	 * date tomorrow (i.e. between 0:00 and 23:59 the day after the current time).
	 * 
	 * @return
	 */
	public List<LendTransaction> findLendTransactionsToLendSoon() {
		final Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		final Calendar startDateMin = (Calendar)today.clone();
		startDateMin.add(Calendar.DAY_OF_MONTH, 1);
		
		final Calendar startDateMax = (Calendar)today.clone();
		startDateMax.add(Calendar.DAY_OF_MONTH, 2);
		startDateMax.add(Calendar.MINUTE, -1);
		
		return findLendTransactionsToLendBetween(startDateMin.getTime(), startDateMax.getTime());
	}

	/**
	 * Returns lend transactions in status INITIALIZED or OPENED that have their start
	 * date between pStartDateMin and pStartDateMax.
	 * 
	 * @param pStartDateMin
	 * @param pStartDateMax
	 * @return
	 */
	public List<LendTransaction> findLendTransactionsToLendBetween(final Date pStartDateMin, final Date pStartDateMax) {		
		final LendTransactionStatus initializedStatus = (LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.INITIALIZED_LABEL_CODE);
		final LendTransactionStatus openedStatus = (LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE);
		
		final Long[] statusIds = {initializedStatus.getId(), openedStatus.getId()};
		
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setStartDateMin(pStartDateMin);
		queryBean.setStartDateMax(pStartDateMax);
		queryBean.setStatusIds(statusIds);
		
		return lendTransactionDao.findLendTransactionsList(queryBean);		
	}

	/**
	 * Returns lend transactions in status IN PROGRESS that have their end
	 * date tomorrow (i.e. between 0:00 and 23:59 the day after the current time).
	 * 
	 * @return
	 */
	public List<LendTransaction> findLendTransactionsToReturnSoon() {
		final Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		
		Date temp = today.getTime();
		System.out.println(UiUtils.getDateTimeAsString(temp, I18nUtils.getDefaultLocale()));
		
		final Calendar endDateMin = (Calendar)today.clone();
		endDateMin.add(Calendar.DAY_OF_MONTH, 1);
		
		final Calendar endDateMax = (Calendar)today.clone();
		endDateMax.add(Calendar.DAY_OF_MONTH, 2);
		endDateMax.add(Calendar.MINUTE, -1);
		
		return findLendTransactionsToReturnBetween(endDateMin.getTime(), endDateMax.getTime());
	}

	/**
	 * Returns lend transactions in status IN PROGRESS that have their end
	 * date between pEndDateMin and pEndDateMax.
	 * 
	 * @param pEndDateMin
	 * @param pEndDateMax
	 * @return
	 */
	public List<LendTransaction> findLendTransactionsToReturnBetween(final Date pEndDateMin, final Date pEndDateMax) {		
		final LendTransactionStatus inProgressStatus = (LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		
		final Long[] statusIds = {inProgressStatus.getId()};
		
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setEndDateMin(pEndDateMin);
		queryBean.setEndDateMax(pEndDateMax);
		queryBean.setStatusIds(statusIds);
		
		return lendTransactionDao.findLendTransactionsList(queryBean);		
	}

	public long countLendTransactionsWaitingForInput(final Long pPersonId) {
		return lendTransactionDao.countLendTransactionsWaitingForInput(pPersonId);
	}

	public long countLendTransactionsForItemAndPerson(final Long pItemId, final Long pPersonId, final Long pStatusId) {
		final LendTransactionDaoQueryBean queryBean = getLendTransactionsForItemAndPersonQueryBean(pItemId, pPersonId, pStatusId, 0, 0);
		return lendTransactionDao.countLendTransactions(queryBean);
	}
	
	private LendTransactionDaoQueryBean getLendTransactionsForItemAndPersonQueryBean(final Long pItemId, final Long pPersonId, final Long pStatusId, final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setItemId(pItemId);
		queryBean.setBorrowerOrLenderId(pPersonId);
		if (pStatusId != null) {
			if (LendTransactionConsts.UNCOMPLETED_STATUS_SELECT_ITEM_VALUE.equals(pStatusId)) {
				queryBean.setStatusIds(getUncompletedStatusIds()); 
			}
			else {
				queryBean.setStatusIds(new Long[]{pStatusId});
			}
		}
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return queryBean;
	}

	private Long[] getUncompletedStatusIds() {
		final ListValue initialized = listValueDao.findListValue(LendTransactionStatus.INITIALIZED_LABEL_CODE);
		final ListValue opened = listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE);
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		return new Long[]{initialized.getId(), opened.getId(), inProgress.getId()};
	}
	
	public ListWithRowCount findCurrentUserLendTransactions(final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setBorrowerOrLenderId(PersonUtils.getCurrentPersonId());
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}

	public long countCurrentUserLendTransactions() {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setBorrowerOrLenderId(PersonUtils.getCurrentPersonId());
		return lendTransactionDao.countLendTransactions(queryBean);
	}
	
	public ListWithRowCount findCurrentUserUncompletedLendTransactions(final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setBorrowerOrLenderId(PersonUtils.getCurrentPersonId());
		
		final ListValue opened = listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE);
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final Long[] uncompletedIds = new Long[]{opened.getId(), inProgress.getId()};
		queryBean.setStatusIds(uncompletedIds);
		
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}

	public ListWithRowCount findCurrentUserInProgressLendTransactionsAsBorrower(final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setBorrowerId(PersonUtils.getCurrentPersonId());
		
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final Long[] inProgressIds = new Long[]{inProgress.getId()};
		queryBean.setStatusIds(inProgressIds);
		
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}

	public ListWithRowCount findCurrentUserInProgressLendTransactionsAsLender(final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setLenderId(PersonUtils.getCurrentPersonId());
		
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final Long[] inProgressIds = new Long[]{inProgress.getId()};
		queryBean.setStatusIds(inProgressIds);
		
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
	}
	
	public long countCurrentUserUncompletedLendTransactions() {		
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setBorrowerOrLenderId(PersonUtils.getCurrentPersonId());
		
		final ListValue opened = listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE);
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final Long[] uncompletedIds = new Long[]{opened.getId(), inProgress.getId()};
		queryBean.setStatusIds(uncompletedIds);
		
		return lendTransactionDao.countLendTransactions(queryBean);
	}

	/**
	 * This operation will create a lend transaction with a status initialized.

	 * @param pBorrower
	 * @param pBorrowerName
	 * @param pItem
	 * @param pLendRequest
	 * @param pStartDate
	 * @param pEndDate
	 * @param pDoNotStart: if set to true, will not mark the transaction as "in progress" even if the the dates indicate it should be
	 * 					   in progress. This will avoid having an object with several "in progress" transactions. 
	 * @return
	 * @throws LendTransactionException 
	 */
	public Long createLendTransaction(final Person pBorrower, final String pBorrowerName, final Item pItem, final LendRequest pLendRequest,
			final Date pStartDate, final Date pEndDate, final Boolean pDoNotStart) throws LendTransactionException {
		try {
			
			if (pBorrower == null && StringUtils.isNullOrEmpty(pBorrowerName)) {
				throw new LendTransactionException("pBorrower or pBorrowername must be defined");
			}
			else if (pBorrower != null && !StringUtils.isNullOrEmpty(pBorrowerName)) {
				throw new LendTransactionException("Only one of pBorrower or pBorrowername must be defined");
			}
			
			final LendTransaction lendTransaction = new LendTransaction();
			lendTransaction.setTitle(pItem.getTitle());
			lendTransaction.setLender(pItem.getOwner());
			lendTransaction.setBorrower(pBorrower);
			lendTransaction.setBorrowerName(pBorrowerName);
			lendTransaction.setItem(pItem);
			if (pBorrower != null) {
				lendTransaction.addCommentRecipient(pBorrower);
			}
			lendTransaction.addCommentRecipient(pItem.getOwner());
			lendTransaction.setCreationDate(new Date());
			lendTransaction.setStartDate(pStartDate);
			lendTransaction.setEndDate(pEndDate);
			lendTransaction.setLendRequest(pLendRequest);
			// If the lend request exists, then the owner still need to accept/refuse.
			if (pLendRequest != null) {
				lendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.INITIALIZED_LABEL_CODE));
			}
			// Otherwise he lent it.
			else {
				final Date now = new Date();
				if (pEndDate != null && pEndDate.before(now)) { 
					lendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.COMPLETED_LABEL_CODE));
				}
				else if (!Boolean.TRUE.equals(pDoNotStart) &&
						 pStartDate != null && pStartDate.before(now)) { 
					lendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE));
				}
				else {
					lendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE));
				}
			}
			lendTransaction.setConflict(Boolean.FALSE);
			
			Long lendTransactionId = lendTransactionDao.createLendTransaction(lendTransaction);
			
			return lendTransactionId;
		} 
		catch (Exception e) {
			throw new LendTransactionException(e);
		}
	}
	
	public Long createLendTransaction(final LendRequest pLendRequest) throws LendTransactionException {
		CoreUtils.assertNotNull(pLendRequest);
		return createLendTransaction(pLendRequest.getRequester(),
				null,
				pLendRequest.getItem(),
				pLendRequest,
				pLendRequest.getStartDate(),
				pLendRequest.getEndDate(),
				null);
	}	
	
	public void updateLendTransaction(final LendTransaction pLendTransaction) {
		lendTransactionDao.updateLendTransaction(pLendTransaction);
	} 

	private Person getCurrentPerson() {
		return personService.getCurrentPerson();
	}

	public List<OrderedListValue> getStatuses() {
		return listValueDao.findOrderedListValue(LendTransactionStatus.class);
	}

	/**
	 * Lend to a user of the system.
	 * 
	 * @param pItemId
	 * @param pBorrowerId
	 * @param pBorrowDate
	 * @throws ItemException 
	 */
	public Long updateLendItem(final Long pItemId, final Long pBorrowerId, final Date pBorrowDate, final Date pEndDate) throws LendTransactionException {
		if (log.isInfoEnabled()) {
			log.info("Lending item to borrower ID: " + pBorrowerId);
		}
		final Item item = itemService.findItem(pItemId);
		itemService.assertCurrentUserAuthorizedToEdit(item);
		
		final Person borrower = personService.findPerson(pBorrowerId);
		
	    final boolean itemAlreadyBorrowed = item.isBorrowed();
		
		Long lendTransactionId = createLendTransaction(borrower, null, item, null,
				pBorrowDate, pEndDate, Boolean.valueOf(itemAlreadyBorrowed));
		
		final Date now = new Date();
		
		// Only mark as borrowed and send email dates actually indicate it is
		// currently lent.
		if (!itemAlreadyBorrowed &&
			(pBorrowDate == null || pBorrowDate.before(now)) &&
			(pEndDate == null || pEndDate.after(now))) {
			// TODO: check if enabled and allowed to borrow !?
			item.setBorrowed(borrower, pBorrowDate);
			itemService.updateItem(item);
			
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("borrowerFirstName", borrower.getFirstName());
			objects.put("lenderFirstName", PersonUtils.getCurrentPersonFirstName());
			objects.put("lenderLastName", PersonUtils.getCurrentPersonLastName());
			objects.put("lenderDisplayName", PersonUtils.getCurrentPersonDisplayName());
			objects.put("itemTitle", item.getTitle());
			objects.put("lendTransactionUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(),
					PagesURL.LEND_TRANSACTION_OVERVIEW,
					PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
					lendTransactionId.toString()));
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lend/lend/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(borrower.getEmail(), borrower.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			mailManager.send(Configuration.getNoReplySenderName(), 
					         Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": objet emprunté",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);
		}
		return lendTransactionId;
	}
	
	/**
	 * Lend to someone not using the system.
	 * 
	 * @param pItemId
	 * @param pBorrowerId
	 * @param pBorrowDate
	 * @param pEndDate
	 * @throws LendTransactionException 
	 */
	public Long updateLendItem(final Long pItemId, final String pBorrowerName, final Date pBorrowDate, final Date pEndDate)
			throws LendTransactionException {
		if (log.isInfoEnabled()) {
			log.info("Lending item to borrower name: " + pBorrowerName);
		}
		final Item item = itemService.findItem(pItemId);
		final boolean itemAlreadyBorrowed = item.isBorrowed();
		itemService.assertCurrentUserAuthorizedToEdit(item);
		item.setBorrowerName(pBorrowerName);
		item.setBorrower(null);
		item.setBorrowDate(pBorrowDate);
		itemService.updateItem(item);
		return createLendTransaction(null, pBorrowerName, item, null,
				pBorrowDate, pEndDate, Boolean.valueOf(itemAlreadyBorrowed));
		// No email to send out since the borrower is not a user of the system.
	}

	public boolean isInitializedToOpenedAvailable(final LendTransaction pLendTransaction) {
		final LendTransactionStatus status = pLendTransaction.getStatus();
		final boolean isCurrentUserLender = pLendTransaction.getLender().getId().equals(PersonUtils.getCurrentPersonId());
		final boolean isCurrentUserOwner = pLendTransaction.getItem() != null &&
		                                   pLendTransaction.getItem().getOwner().getId().equals(PersonUtils.getCurrentPersonId());
		return LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(status.getLabelCode()) && isCurrentUserLender && isCurrentUserOwner;
	}

	public boolean isInitializedOrOpenedToCanceledAvailable(final LendTransaction pLendTransaction) {
		final LendTransactionStatus status = pLendTransaction.getStatus();
		final boolean isCurrentUserLender = pLendTransaction.getLender().getId().equals(PersonUtils.getCurrentPersonId());
		final boolean isCurrentUserBorrower = pLendTransaction.getBorrower() != null &&
				pLendTransaction.getBorrower().getId().equals(PersonUtils.getCurrentPersonId());
		
		return (isCurrentUserBorrower && (LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(status.getLabelCode()) || LendTransactionStatus.OPENED_LABEL_CODE.equals(status.getLabelCode()))) ||
			   (isCurrentUserLender && LendTransactionStatus.OPENED_LABEL_CODE.equals(status.getLabelCode()));
	}

	public boolean isInitializedToCanceledRefusedAvailable(final LendTransaction pLendTransaction) {
		final LendTransactionStatus status = pLendTransaction.getStatus();
		final boolean isCurrentUserLender = pLendTransaction.getLender().getId().equals(PersonUtils.getCurrentPersonId());
		
		return isCurrentUserLender && LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(status.getLabelCode());
	}

	public boolean isOpenedToInProgressAvailable(final LendTransaction pLendTransaction) {
		final LendTransactionStatus status = pLendTransaction.getStatus();
		final boolean isCurrentUserOwner = pLendTransaction.getItem() != null &&
		                                   pLendTransaction.getItem().getOwner().getId().equals(PersonUtils.getCurrentPersonId());
		final boolean isCurrentUserLender = pLendTransaction.getLender().getId().equals(PersonUtils.getCurrentPersonId());
		
		return LendTransactionStatus.OPENED_LABEL_CODE.equals(status.getLabelCode()) && 
		            isCurrentUserOwner && 
		            isCurrentUserLender &&
					!pLendTransaction.getItem().isBorrowed();	
	}
	
	public boolean isGiveOrSellAvailable(final LendTransaction pLendTransaction) {
		final String statusCode = pLendTransaction.getStatus().getLabelCode();
		final boolean isCurrentUserOwner = pLendTransaction.getItem() != null && 
										   pLendTransaction.getItem().getOwner().getId().equals(PersonUtils.getCurrentPersonId());
		final boolean isCurrentUserLender = pLendTransaction.getLender().getId().equals(PersonUtils.getCurrentPersonId());
		
		return isCurrentUserOwner && 
		       isCurrentUserLender &&
		        pLendTransaction.getItem().isToGiveOrSell() &&
				pLendTransaction.getBorrower() != null &&
				((!pLendTransaction.getItem().isBorrowed() && (LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(statusCode) || LendTransactionStatus.OPENED_LABEL_CODE.equals(statusCode))) ||
				 LendTransactionStatus.IN_PROGRESS_LABEL_CODE.equals(statusCode));
	}

	public boolean isInProgressToCompletedAvailable(final LendTransaction pLendTransaction) {
		final LendTransactionStatus status = pLendTransaction.getStatus();
		final boolean isCurrentUserLender = pLendTransaction.getLender().getId().equals(PersonUtils.getCurrentPersonId());
		
		return LendTransactionStatus.IN_PROGRESS_LABEL_CODE.equals(status.getLabelCode()) && isCurrentUserLender;
	}	

	public String getItemInProgressLendTransactionUrl(final Item pItem) throws LendTransactionException {
		final LendTransaction lt = findInProgressLendTransactionForItem(pItem);
		if (lt != null) {
			return JsfUtils.getFullUrl(
					PagesURL.LEND_TRANSACTION_OVERVIEW,
					PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
					lt.getId().toString());
		}
		else {
			if (log.isWarnEnabled()) {
				log.warn("No in progress lend transaction for item: " + pItem.getId());
			}
			return null;
		}			
	}

	public ListWithRowCount findLendTransactionsWaitingEvaluationFromPerson(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		CoreUtils.assertNotNull(pPersonId);
		final Long completedId = listValueDao.findListValue(LendTransactionStatus.COMPLETED_LABEL_CODE).getId();
		
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		queryBean.setToEvaluateByPersonId(pPersonId);
		queryBean.setCompletedStatusId(completedId);
		
		return lendTransactionDao.findLendTransactions(queryBean);
	}

	public long countLendTransactionsWaitingEvaluationFromPerson(final Long pPersonId) {
		CoreUtils.assertNotNull(pPersonId);
		final Long completedId = listValueDao.findListValue(LendTransactionStatus.COMPLETED_LABEL_CODE).getId();
		
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setToEvaluateByPersonId(pPersonId);
		queryBean.setCompletedStatusId(completedId);
		
		return lendTransactionDao.countLendTransactions(queryBean);
	}

	/////////////////////////////////////////////////////////
	// Access control
	
	public boolean isCurrentUserAuthorizedToView(final LendTransaction pLendTransaction) {
		return isUserAuthorizedToView(getCurrentPerson(), pLendTransaction);
	}

	public boolean isUserAuthorizedToView(final Person pPerson, final LendTransaction pLendTransaction) {
		CoreUtils.assertNotNull(pLendTransaction);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		
		if (isUserAuthorizedToEdit(pPerson, pLendTransaction)) {
			return true;
		}
		final Person lender = pLendTransaction.getLender();
		final Person borrower = pLendTransaction.getBorrower();
		
		// Lender and borrower can view.
		return pPerson != null && 
			(pPerson.equals(lender) || pPerson.equals(borrower));
	}
	
	public void assertCurrentUserAuthorizedToView(final LendTransaction pLendTransaction) {
		if (!isCurrentUserAuthorizedToView(pLendTransaction)) {
			throw new SecurityException("Current user is not authorized to view lend transaction");
		}
	}
	
	public void assertUserAuthorizedToView(final Person pPerson, final LendTransaction pLendTransaction) {
		if (!isUserAuthorizedToView(pPerson, pLendTransaction)) {
			throw new SecurityException("User is not authorized to view lend transaction");
		}
	}
	
	public boolean isCurrentUserAuthorizedToEdit(final LendTransaction pLendTransaction) {
		return isUserAuthorizedToEdit(getCurrentPerson(), pLendTransaction);
	}
	
	public boolean isUserAuthorizedToEdit(final Person pPerson, final LendTransaction pLendTransaction) {
		CoreUtils.assertNotNull(pLendTransaction);
		if (!SecurityUtils.isLoggedIn()) {
			return false;
		}
		if (pPerson == null || pPerson.getUser() == null) {
			return false;
		}
		final String statusCode = pLendTransaction.getStatus().getLabelCode(); 
		if (LendTransactionStatus.CANCELED_LABEL_CODE.equals(statusCode) ||
				LendTransactionStatus.COMPLETED_LABEL_CODE.equals(statusCode)) {
			return false;
		}
		if (pPerson.getUser() != null &&
			pPerson.getUser().isAdmin()) {
			return true;
		}
		final Person lender = pLendTransaction.getLender();
		// Only lender can edit.
		return lender != null && lender.equals(pPerson);
	}

	public void assertCurrentUserAuthorizedToEdit(final LendTransaction pLendTransaction) {
		if (!isCurrentUserAuthorizedToEdit(pLendTransaction)) {
			throw new SecurityException("Current user is not authorized to edit lend transaction");
		}
	}

	public boolean isCurrentUserAuthorizedToAdd() {
		final Person currentPerson = getCurrentPerson();
		if (currentPerson != null && currentPerson.getUser() != null) {
			return true;
		}
		return false;
	}

	public void assertCurrentUserAuthorizedToAdd(final LendTransaction pLendTransaction) {
		if (!isCurrentUserAuthorizedToAdd()) {
			throw new SecurityException("Current user is not authorized to add lend transaction");
		}
	}

	public boolean isCurrentUserAuthorizedToDelete(final LendTransaction pLendTransaction) {
		return isCurrentUserAuthorizedToEdit(pLendTransaction);
	}

	public void assertCurrentUserAuthorizedToDelete(final LendTransaction pLendTransaction) {
		if (!isCurrentUserAuthorizedToDelete(pLendTransaction)) {
			throw new SecurityException("Current user is not authorized to delete lend transaction");
		}
	}

	// Access control - end
	/////////////////////////////////////////////////////////
}
