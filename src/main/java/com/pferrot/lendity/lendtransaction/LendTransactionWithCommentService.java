package com.pferrot.lendity.lendtransaction;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.comment.CommentService;
import com.pferrot.lendity.comment.exception.CommentException;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.UiUtils;

/**
 * Required to avoid circular dependencies between LendTransactionService
 * and CommentService.
 * 
 * @author pferrot
 *
 */
public class LendTransactionWithCommentService {

	private final static Log log = LogFactory.getLog(LendTransactionWithCommentService.class);
	
	private LendTransactionService lendTransactionService;
	private CommentService commentService;
	private ItemService itemService;
	private ListValueDao listValueDao;
	private LendTransactionDao lendTransactionDao;
	
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public void setLendTransactionDao(LendTransactionDao lendTransactionDao) {
		this.lendTransactionDao = lendTransactionDao;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}
	
	public void updateLendTransactionDates(final LendTransaction pLendTransaction, final Date pStartDate, final Date pEndDate) throws LendTransactionException {
		try {
			final Date startDateBefore = pLendTransaction.getStartDate();
			final Date endDateBefore = pLendTransaction.getEndDate();
			
			// Nothing changed.
			if (startDateBefore.equals(pStartDate) && endDateBefore.equals(pEndDate)) {
				return;
			}
			// Only end date changed.
			else if (startDateBefore.equals(pStartDate)) {
				pLendTransaction.setEndDate(pEndDate);
				lendTransactionDao.updateLendTransaction(pLendTransaction);
				
				final String endDateBeforeLabel = UiUtils.getDateAsString(endDateBefore, I18nUtils.getDefaultLocale()); 
				final String endDateAfterLabel = UiUtils.getDateAsString(pEndDate, I18nUtils.getDefaultLocale());
				
				commentService.createSystemCommentOnLendTransactionWithAC("TODO new end date: " + endDateAfterLabel + " (was " + endDateBeforeLabel + ")", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
				
			}
			// Only start date changed.
			else if (endDateBefore.equals(pEndDate)) {
				pLendTransaction.setStartDate(pStartDate);
				lendTransactionDao.updateLendTransaction(pLendTransaction);
				
				final String startDateBeforeLabel = UiUtils.getDateAsString(startDateBefore, I18nUtils.getDefaultLocale()); 
				final String startDateAfterLabel = UiUtils.getDateAsString(pStartDate, I18nUtils.getDefaultLocale());
				
				commentService.createSystemCommentOnLendTransactionWithAC("TODO new start date: " + startDateAfterLabel + " (was " + startDateBeforeLabel + ")", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
			}
			// Both changed.
			else {
				pLendTransaction.setStartDate(pStartDate);
				pLendTransaction.setEndDate(pEndDate);
				lendTransactionDao.updateLendTransaction(pLendTransaction);
				
				final String startDateBeforeLabel = UiUtils.getDateAsString(startDateBefore, I18nUtils.getDefaultLocale()); 
				final String startDateAfterLabel = UiUtils.getDateAsString(pStartDate, I18nUtils.getDefaultLocale());
				final String endDateBeforeLabel = UiUtils.getDateAsString(endDateBefore, I18nUtils.getDefaultLocale()); 
				final String endDateAfterLabel = UiUtils.getDateAsString(pEndDate, I18nUtils.getDefaultLocale());
				
				commentService.createSystemCommentOnLendTransactionWithAC("TODO new start date: " + startDateAfterLabel + " (was " + startDateBeforeLabel + ")\nnew end date: " + endDateAfterLabel + " (was " + endDateBeforeLabel + ")", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
			}		
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
		
	}

	
	public void updateInitializedOrOpenedToCanceled(final LendTransaction pLendTransaction) throws LendTransactionException {
		try {
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(lendTransactionService.isInitializedOrOpenedToCanceledAvailable(pLendTransaction));
			
			pLendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.CANCELED_LABEL_CODE));
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.
			commentService.createSystemCommentOnLendTransactionWithAC("TODO transaction canceled", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}

	public void updateInitializedToCanceledRefused(final LendTransaction pLendTransaction) throws LendTransactionException {
		try {
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(lendTransactionService.isInitializedToCanceledRefusedAvailable(pLendTransaction));
			
			pLendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.CANCELED_LABEL_CODE));
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.
			commentService.createSystemCommentOnLendTransactionWithAC("TODO transaction canceled (refused)", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), false);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}

	public void updateInitializedToOpened(final LendTransaction pLendTransaction) throws LendTransactionException {
		try {
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(lendTransactionService.isInitializedToOpenedAvailable(pLendTransaction));
			
			pLendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE));
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.
			// !!! Email already sent by lend request service.
			commentService.createSystemCommentOnLendTransactionWithAC("TODO request accepted", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), false);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}

	// The the owner indicates the item is lent.
	public void updateOpenedToInProgress(final LendTransaction pLendTransaction) throws LendTransactionException {
		try {
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(lendTransactionService.isOpenedToInProgressAvailable(pLendTransaction));
			
			final Item item = pLendTransaction.getItem();
			if (item != null) {
				if (item.isBorrowed()) {
					throw new LendTransactionException("Item " + item.getId() + " is already lent");
				}
				
				if (pLendTransaction.getBorrower() != null) {
					item.setBorrowed(pLendTransaction.getBorrower(), new Date());
				}
				else {
					item.setBorrowed(pLendTransaction.getBorrowerName(), new Date());
				}
				
				itemService.updateItem(item);
			}
			else {
				if (log.isWarnEnabled()) {
					log.warn("Opened to in progress: object not available on lend transaction " + pLendTransaction.getId());
				}
			}
			
			pLendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE));
			
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.		
			commentService.createSystemCommentOnLendTransactionWithAC("TODO transaction in progress", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}

	public void updateGiveOrSellItem(final LendTransaction pLendTransaction) throws LendTransactionException {
		try {
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(lendTransactionService.isGiveOrSellAvailable(pLendTransaction));
			
			final Item item = pLendTransaction.getItem();
			if (item != null) {
				if (item.isBorrowed()) {
					throw new LendTransactionException("Item " + item.getId() + " is lent - cannot be given");
				}
				
				item.setOwner(pLendTransaction.getBorrower());
				item.setToGiveForFree(Boolean.FALSE);
				item.setSalePrice(null);
				item.setDeposit(null);
				item.setVisibility((ItemVisibility)listValueDao.findListValue(ItemVisibility.PRIVATE));
				item.setAcquisitionDate(new Date());
				
				itemService.updateItem(item);
			}
			else {
				if (log.isWarnEnabled()) {
					log.warn("Give or sell: object not available on lend transaction " + pLendTransaction.getId());
				}
			}
			
			pLendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.COMPLETED_LABEL_CODE));
			
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.		
			commentService.createSystemCommentOnLendTransactionWithAC("TODO transaction completed - object transfered", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}
	
	public void updateInProgressToCompleted(final LendTransaction pLendTransaction) throws LendTransactionException {
		try {
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(lendTransactionService.isInProgressToCompletedAvailable(pLendTransaction));
			
			final Item item = pLendTransaction.getItem();
			if (item != null) {
				item.setLendBack();
				itemService.updateItem(item);
			}
			else {
				if (log.isWarnEnabled()) {
					log.warn("In progress to completed: object not available on lend transaction " + pLendTransaction.getId());
				}
			}
			
			pLendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.COMPLETED_LABEL_CODE));
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.
			commentService.createSystemCommentOnLendTransactionWithAC("TODO transaction completed", pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}

	public void updateSetEvaluationByBorrower(final LendTransaction pLendTransaction, final Evaluation pEvaluation) throws LendTransactionException {
		try {
			CoreUtils.assertNotNull(pLendTransaction);
			CoreUtils.assertNotNull(pEvaluation);
			
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(pLendTransaction.getEvaluationByBorrower() == null);
			
			pLendTransaction.setEvaluationByBorrower(pEvaluation);
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.
			commentService.createSystemCommentOnLendTransactionWithAC("TODO evaluation by borrower: " + pEvaluation.getScore() + "\n" + pEvaluation.getText() , pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}

	public void updateSetEvaluationByLender(final LendTransaction pLendTransaction, final Evaluation pEvaluation) throws LendTransactionException {
		try {
			CoreUtils.assertNotNull(pLendTransaction);
			CoreUtils.assertNotNull(pEvaluation);
			
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			CoreUtils.assertTrue(pLendTransaction.getEvaluationByLender() == null);
			
			pLendTransaction.setEvaluationByLender(pEvaluation);
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			// TODO: add comment in lend transaction.
			commentService.createSystemCommentOnLendTransactionWithAC("TODO evaluation by lender: " + pEvaluation.getScore() + "\n" + pEvaluation.getText() , pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}
}
