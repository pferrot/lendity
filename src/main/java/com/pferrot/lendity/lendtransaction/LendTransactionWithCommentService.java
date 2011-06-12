package com.pferrot.lendity.lendtransaction;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.comment.CommentService;
import com.pferrot.lendity.comment.exception.CommentException;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.evaluation.EvaluationUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.Evaluation;
import com.pferrot.lendity.model.Group;
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
			final long startDateAfterMs = pStartDate.getTime();
			final long endDateAfterMs = pEndDate.getTime();
			final Date startDateBefore = pLendTransaction.getStartDate();
			final long startDateBeforeMs = startDateBefore.getTime();
			final Date endDateBefore = pLendTransaction.getEndDate();
			final long endDateBeforeMs = endDateBefore.getTime();
			
			// Nothing changed.
			if (startDateAfterMs == startDateBeforeMs && endDateBeforeMs == endDateAfterMs) {
				return;
			}
			// Only end date changed.
			else if (startDateBeforeMs == startDateAfterMs) {
				pLendTransaction.setEndDate(pEndDate);
				lendTransactionDao.updateLendTransaction(pLendTransaction);
				
				final String endDateBeforeLabel = UiUtils.getDateAsString(endDateBefore, I18nUtils.getDefaultLocale()); 
				final String endDateAfterLabel = UiUtils.getDateAsString(pEndDate, I18nUtils.getDefaultLocale());
				
				final Locale locale = I18nUtils.getDefaultLocale();
				final String message = I18nUtils.getMessageResourceString("lendTransaction_endDateChanged", new Object[]{endDateAfterLabel, endDateBeforeLabel}, locale);
				commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
				
				
			}
			// Only start date changed.
			else if (endDateBeforeMs == endDateAfterMs) {
				pLendTransaction.setStartDate(pStartDate);
				lendTransactionDao.updateLendTransaction(pLendTransaction);
				
				final String startDateBeforeLabel = UiUtils.getDateAsString(startDateBefore, I18nUtils.getDefaultLocale()); 
				final String startDateAfterLabel = UiUtils.getDateAsString(pStartDate, I18nUtils.getDefaultLocale());
				
				final Locale locale = I18nUtils.getDefaultLocale();
				final String message = I18nUtils.getMessageResourceString("lendTransaction_startDateChanged", new Object[]{startDateAfterLabel, startDateBeforeLabel}, locale);
				commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
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
				
				final Locale locale = I18nUtils.getDefaultLocale();
				final String message1 = I18nUtils.getMessageResourceString("lendTransaction_startDateChanged", new Object[]{startDateAfterLabel, startDateBeforeLabel}, locale);
				final String message2 = I18nUtils.getMessageResourceString("lendTransaction_endDateChanged", new Object[]{endDateAfterLabel, endDateBeforeLabel}, locale);
				commentService.createSystemCommentOnLendTransactionWithAC(message1 + "\n" + message2, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
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
			
			final Locale locale = I18nUtils.getDefaultLocale();
			final String message = I18nUtils.getMessageResourceString("lendTransaction_transactionCancelled", locale);
			commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
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
			
			final Locale locale = I18nUtils.getDefaultLocale();
			final String message = I18nUtils.getMessageResourceString("lendTransaction_transactionRefused", locale);
			commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), false);
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
			
			final Locale locale = I18nUtils.getDefaultLocale();
			final String message = I18nUtils.getMessageResourceString("lendTransaction_transactionAccepted", locale);
			commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), false);
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
			
			final Locale locale = I18nUtils.getDefaultLocale();
			final String message = I18nUtils.getMessageResourceString("lendTransaction_transactionInProgress", locale);
			commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}

	public void updateGiveOrSellItem(final LendTransaction pLendTransaction) throws LendTransactionException {
		try {
			lendTransactionDao.refreshLendTransaction(pLendTransaction);
			pLendTransaction.setItemTransfered(Boolean.TRUE);
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
				item.setGroupsAuthorized(new HashSet<Group>());
				item.setAcquisitionDate(new Date());
				
				
				itemService.updateItem(item);
				lendTransactionService.updateLendTransaction(pLendTransaction);
			}
			else {
				if (log.isWarnEnabled()) {
					log.warn("Give or sell: object not available on lend transaction " + pLendTransaction.getId());
				}
			}
			
			pLendTransaction.setStatus((LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.COMPLETED_LABEL_CODE));
			
			lendTransactionService.updateLendTransaction(pLendTransaction);
			
			final Locale locale = I18nUtils.getDefaultLocale();
			final String message = I18nUtils.getMessageResourceString("lendTransaction_transactionCompletedTransfered", locale);
			commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
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
			
			final Locale locale = I18nUtils.getDefaultLocale();
			final String message = I18nUtils.getMessageResourceString("lendTransaction_transactionCompleted", locale);
			commentService.createSystemCommentOnLendTransactionWithAC(message, pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
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
			
			final String evalLabel = EvaluationUtils.getEvaluationLabel(pEvaluation.getScore()); 
			final Locale locale = I18nUtils.getDefaultLocale();
			String message = null;
			if (Boolean.TRUE.equals(pLendTransaction.getItemTransfered())) {
				message = I18nUtils.getMessageResourceString("lendTransaction_transactionEvaluatedByBorrower2", new Object[]{evalLabel}, locale);
			}
			else {
				message = I18nUtils.getMessageResourceString("lendTransaction_transactionEvaluatedByBorrower", new Object[]{evalLabel}, locale);
			}
			commentService.createSystemCommentOnLendTransactionWithAC(message + "\n" + pEvaluation.getText(), pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
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
			
			final String evalLabel = EvaluationUtils.getEvaluationLabel(pEvaluation.getScore()); 
			final Locale locale = I18nUtils.getDefaultLocale();
			String message = null;
			if (Boolean.TRUE.equals(pLendTransaction.getItemTransfered())) {
				message = I18nUtils.getMessageResourceString("lendTransaction_transactionEvaluatedByLender2", new Object[]{evalLabel}, locale);
			}
			else {
				message = I18nUtils.getMessageResourceString("lendTransaction_transactionEvaluatedByLender", new Object[]{evalLabel}, locale);
			}
			commentService.createSystemCommentOnLendTransactionWithAC(message + "\n" + pEvaluation.getText(), pLendTransaction.getId(), PersonUtils.getCurrentPersonId(), true);
		}
		catch (CommentException e) {
			throw new LendTransactionException(e);
		}
	}
}
