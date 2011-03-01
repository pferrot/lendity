package com.pferrot.lendity.lendtransaction;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.LendTransactionDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
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
import com.pferrot.security.SecurityUtils;

public class LendTransactionService {

	private final static Log log = LogFactory.getLog(LendTransactionService.class);
	
	private LendTransactionDao lendTransactionDao;
	private ListValueDao listValueDao;
	private PersonDao personDao;
	private ItemDao itemDao;
	private MailManager mailManager;
	private PersonService personService;

	public void setMailManager(final MailManager pMailManager) {
		this.mailManager = pMailManager;
	}	
	
	public void setLendTransactionDao(final LendTransactionDao pLendTransactionDao) {
		this.lendTransactionDao = pLendTransactionDao;
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
