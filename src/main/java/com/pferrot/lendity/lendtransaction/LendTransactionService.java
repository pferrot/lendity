package com.pferrot.lendity.lendtransaction;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.dao.ItemDao;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.dao.bean.LendTransactionDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.LendRequest;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.model.ListValue;
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
	
	public LendTransaction findInProgressLendTransactionForItem(final InternalItem pInternalItem) throws LendTransactionException {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setInternalItemId(pInternalItem.getId());
		final ListValue inProgress = listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final Long[] inProgressIds = new Long[]{inProgress.getId()};
		queryBean.setStatusIds(inProgressIds);
		final ListWithRowCount lwrc = lendTransactionDao.findLendTransactions(queryBean);
		if (lwrc.getRowCount() > 1) {
			throw new LendTransactionException("Should not have found more than 1 in progress lend transaction" +
					" for item = " + pInternalItem.getId());
		}
		else if (lwrc.getRowCount() == 0) {
			return null;
		}
		else {
			return (LendTransaction)lwrc.getList().get(0);
		}
	}

	public ListWithRowCount findCurrentUserLendTransactions(final int pFirstResult, final int pMaxResults) {
		final LendTransactionDaoQueryBean queryBean = new LendTransactionDaoQueryBean();
		queryBean.setBorrowerOrLenderId(PersonUtils.getCurrentPersonId());
		queryBean.setFirstResult(pFirstResult);
		queryBean.setMaxResults(pMaxResults);
		return lendTransactionDao.findLendTransactions(queryBean);
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
	 * @param pItem
	 * @param pLendRequest
	 * @param pStartDate
	 * @param pEndDate
	 * @return
	 * @throws LendTransactionException 
	 */
	public Long createLendTransaction(final Person pBorrower, final InternalItem pItem, final LendRequest pLendRequest,
			final Date pStartDate, final Date pEndDate) throws LendTransactionException {
		try {
			
			final LendTransaction lendTransaction = new LendTransaction();
			lendTransaction.setLender(pItem.getOwner());
			lendTransaction.setBorrower(pBorrower);
			lendTransaction.setItem(pItem);
			lendTransaction.addCommentRecipient(pBorrower);
			lendTransaction.addCommentRecipient(pItem.getOwner());
			lendTransaction.setCreationDate(new Date());
			lendTransaction.setStartDate(pStartDate);
			lendTransaction.setEndDate(pEndDate);
			lendTransaction.setLendRequest(pLendRequest);
			lendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.INITIALIZED_LABEL_CODE));
			
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
				pLendRequest.getItem(),
				pLendRequest,
				pLendRequest.getStartDate(),
				pLendRequest.getEndDate());
	}
	
	public void updateLendTransaction(final LendTransaction pLendTransaction) {
		lendTransactionDao.updateLendTransaction(pLendTransaction);
	} 

	private Person getCurrentPerson() {
		return personService.getCurrentPerson();
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
