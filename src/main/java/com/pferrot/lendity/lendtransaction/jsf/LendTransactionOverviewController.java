package com.pferrot.lendity.lendtransaction.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;
import org.springframework.web.servlet.support.JstlUtils;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.evaluation.EvaluationService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.lendrequest.exception.LendRequestException;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/lendtransaction/lendTransactionOverview.jspx"})
public class LendTransactionOverviewController extends AbstractLendTransactionOverviewEditController
{
	private final static Log log = LogFactory.getLog(LendTransactionOverviewController.class);
		
	private EvaluationService evaluationService;
	
	@InitView
	public void initView() {
		// Read the person ID from the request parameter and load the correct person.
		try {
			final String idString = JsfUtils.getRequestParameter(PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID);
			LendTransaction lendTransaction = null;
			if (idString != null) {
				setLendTransactionId(Long.parseLong(idString));
				lendTransaction = getLendTransactionService().findLendTransaction(getLendTransactionId());
				// Access control check.
				if (!getLendTransactionService().isCurrentUserAuthorizedToView(lendTransaction)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (lend transaction view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), lendTransaction = " + idString);
					}
					return;
				}
				setLendTransaction(lendTransaction);
			}
		}
		catch (AccessDeniedException ade) {
			throw ade;
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.PERSONS_LIST);
		}		
	}
	
	public EvaluationService getEvaluationService() {
		return evaluationService;
	}
	
	public void setEvaluationService(EvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}
	
	public boolean isEditAvailable() {
		return getLendTransactionService().isCurrentUserAuthorizedToEdit(getLendTransaction());
	}
	
	public String getLendTransactionEditHref() {
		return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_EDIT,
				PagesURL.LEND_TRANSACTION_EDIT_PARAM_LEND_TRANSACTION_ID,
				getLendTransaction().getId().toString());
	}

	public String getWhatIsNextLabel() {
		final LendTransactionStatus status = getLendTransaction().getStatus();
		final boolean isCurrentUserLender = getLendTransaction().getLender().getId().equals(PersonUtils.getCurrentPersonId());
		final boolean isCurrentUserBorrower = getLendTransaction().getBorrower() != null &&
											  getLendTransaction().getBorrower().getId().equals(PersonUtils.getCurrentPersonId());
		final boolean isItemToGiveOrSell = getLendTransaction().getItem().isToGiveOrSell();
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender must accept or refuse lend request.
			if (isCurrentUserLender) {
				if (!isItemToGiveOrSell) {
					return I18nUtils.getMessageResourceString("lendTransaction_initializedHelpLender", locale);
				}
				else {
					return I18nUtils.getMessageResourceString("lendTransaction_initializedHelpLender2", locale);
				}
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				if (!isItemToGiveOrSell) {
					return I18nUtils.getMessageResourceString("lendTransaction_initializedHelpBorrower", locale);
				}
				else {
					return I18nUtils.getMessageResourceString("lendTransaction_initializedHelpBorrower2", locale);
				}
			}			
		}
		else if (LendTransactionStatus.OPENED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender must lend the object.
			if (isCurrentUserLender) {
				if (!isItemToGiveOrSell) {
					return I18nUtils.getMessageResourceString("lendTransaction_openedHelpLender", locale);
				}
				else {
					return I18nUtils.getMessageResourceString("lendTransaction_openedHelpLender2", locale);
				}
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				if (!isItemToGiveOrSell) {
					return I18nUtils.getMessageResourceString("lendTransaction_openedHelpBorrower", locale);
				}
				else {
					return I18nUtils.getMessageResourceString("lendTransaction_openedHelpBorrower2", locale);
				}
			}			
		}
		else if (LendTransactionStatus.IN_PROGRESS_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender has nothing to do.
			if (isCurrentUserLender) {
				return I18nUtils.getMessageResourceString("lendTransaction_inProgressHelpLender", locale);
			}
			// Borrower must give the object back.
			else if (isCurrentUserBorrower) {
				return I18nUtils.getMessageResourceString("lendTransaction_inProgressHelpBorrower", locale);
			}			
		}
		else if (LendTransactionStatus.COMPLETED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender has nothing to do.
			if (isCurrentUserLender) {
				if (Boolean.FALSE.equals(getLendTransaction().getItemTransfered())) {
					return I18nUtils.getMessageResourceString("lendTransaction_completedHelpLender", locale);
				}
				else {
					return I18nUtils.getMessageResourceString("lendTransaction_completedHelpLender2", locale);
				}
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				if (Boolean.FALSE.equals(getLendTransaction().getItemTransfered())) {
					return I18nUtils.getMessageResourceString("lendTransaction_completedHelpBorrower", locale);
				}
				else {
					return I18nUtils.getMessageResourceString("lendTransaction_completedHelpBorrower2", locale);
				}
			}			
		}
		else if (LendTransactionStatus.CANCELED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender has nothing to do.
			if (isCurrentUserLender) {
				return I18nUtils.getMessageResourceString("lendTransaction_canceledHelpLender", locale);
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				return I18nUtils.getMessageResourceString("lendTransaction_canceledHelpBorrower", locale);
			}			
		}
		else {
			throw new RuntimeException("Unknown status: " + status.getLabelCode());
		}
		
		final Person currentPerson = getPersonService().getCurrentPerson();
		if (currentPerson.getUser().isAdmin()) {
			return "Admins can just watch...";
		}
		else {
			throw new RuntimeException("User should not be here: " + currentPerson.getUser().getUsername());
		}
	}
	
	public boolean isAcceptRequestAvailable() {
		return getLendRequestService().isAcceptLendRequestAvailable(getLendTransaction().getLendRequest());
	}
	
	public String acceptRequest() {
		try {
			getLendRequestService().updateAcceptLendRequest(getLendTransaction().getLendRequest());
			return "success";
		}
		catch (LendRequestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isRefuseRequestAvailable() {
		return getLendRequestService().isRefuseLendRequestAvailable(getLendTransaction().getLendRequest());
	}
	
	public String refuseRequest() {
		try {			
			getLendRequestService().updateRefuseLendRequest(getLendTransaction().getLendRequest());
			return "success";
		}
		catch (LendRequestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isLendAvailable() {
		return getLendTransactionService().isOpenedToInProgressAvailable(getLendTransaction());
	}
	
	public String lend() {
		try {
			getLendTransactionWithCommentService().updateOpenedToInProgress(getLendTransaction());
			return "success";
		}
		catch (LendTransactionException e) {
			throw new RuntimeException(e);
		}
	}

	
	public boolean isGiveOrSellAvailable() {
		return getLendTransactionService().isGiveOrSellAvailable(getLendTransaction());
	}
	
	public String giveOrSell() {
		try {
			getLendTransactionWithCommentService().updateGiveOrSellItem(getLendTransaction());
			return "success";
		}
		catch (LendTransactionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isCancelAvailable() {
		return getLendTransactionService().isInitializedOrOpenedToCanceledAvailable(getLendTransaction());
	}
	
	public String cancel() {
		try {
			// updateOpenedToCanceled does exactly the same.
			getLendRequestService().updateCancelLendRequest(getLendTransaction().getLendRequest());
			return "success";
		}
		catch (LendRequestException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isCompleteAvailable() {
		return getLendTransactionService().isInProgressToCompletedAvailable(getLendTransaction());
	}
	
	public String complete() {
		try {
			getLendTransactionWithCommentService().updateInProgressToCompleted(getLendTransaction());
			return "success";
		}
		catch (LendTransactionException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isEvaluateAvailable() {
		return getLendTransaction().getBorrower() != null &&
			   (getEvaluationService().isEvaluationAsBorrowerAuthorized(PersonUtils.getCurrentPersonId(), getLendTransaction()) ||
			    getEvaluationService().isEvaluationAsLenderAuthorized(PersonUtils.getCurrentPersonId(), getLendTransaction()));
	}

	public String getEvaluationAddHref() {
		return JsfUtils.getFullUrl(PagesURL.EVALUATION_ADD, PagesURL.EVALUATION_ADD_PARAM_LEND_TRANSACTION_ID, getLendTransaction().getId().toString());
	}
}
