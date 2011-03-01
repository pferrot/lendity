package com.pferrot.lendity.lendtransaction.jsf;

import java.util.Date;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/auth/lendtransaction/lendTransactionOverview.jspx"})
public class LendTransactionOverviewController
{
	private final static Log log = LogFactory.getLog(LendTransactionOverviewController.class);
	
	private LendTransactionService lendTransactionService;
	private PersonService personService;
	private Long lendTransactionId;
	private LendTransaction lendTransaction;
	
	@InitView
	public void initView() {
		// Read the person ID from the request parameter and load the correct person.
		try {
			final String idString = JsfUtils.getRequestParameter(PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID);
			LendTransaction lendTransaction = null;
			if (idString != null) {
				lendTransactionId = Long.parseLong(idString);
				lendTransaction = lendTransactionService.findLendTransaction(lendTransactionId);
				// Access control check.
				if (!lendTransactionService.isCurrentUserAuthorizedToView(lendTransaction)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (person view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), lendTransaction = " + idString);
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
	
	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public Long getLendTransactionId() {
		return lendTransactionId;
	}

	public void setLendTransactionId(Long lendTransactionId) {
		this.lendTransactionId = lendTransactionId;
	}

	public LendTransaction getLendTransaction() {
		return lendTransaction;
	}

	public void setLendTransaction(LendTransaction lendTransaction) {
		this.lendTransaction = lendTransaction;
	}
	
	public String getBorrowerOverviewHref() {
		return PersonUtils.getPersonOverviewPageUrl(getLendTransaction().getBorrower().getId().toString());
	}
	
	public String getLenderOverviewHref() {
		return PersonUtils.getPersonOverviewPageUrl(getLendTransaction().getLender().getId().toString());
	}
	
	public String getItemOverviewHref() {
		return ItemUtils.getItemOverviewPageUrl(getLendTransaction().getItem().getId().toString());
	}
	
	public String getStatusLabel() {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return I18nUtils.getMessageResourceString(getLendTransaction().getStatus().getLabelCode(), locale);		
	}

	public String getCreationDateLabel() {
		return UiUtils.getDateAsString(getLendTransaction().getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public String getStartDateLabel() {
		final Date startDate = getLendTransaction().getStartDate();
		if (startDate != null) {
			return UiUtils.getDateAsString(startDate, FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		else {
			return "";
		}
	}

	public String getEndDateLabel() {
		final Date endDate = getLendTransaction().getEndDate();
		if (endDate != null) {
			return UiUtils.getDateAsString(endDate, FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		else {
			return "";
		}
	}

	public boolean isBorrowerHrefAvailable() {
		return getLendTransaction().getBorrower() != null;
	}

	public String getWhatIsNextLabel() {
		final LendTransactionStatus status = getLendTransaction().getStatus();
		final boolean isCurrentUserLender = getLendTransaction().getLender().getId().equals(PersonUtils.getCurrentPersonId());
		final boolean isCurrentUserBorrower = getLendTransaction().getBorrower() != null &&
											  getLendTransaction().getBorrower().getId().equals(PersonUtils.getCurrentPersonId());
		
		if (LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender must accept or refuse lend request.
			if (isCurrentUserLender) {
				return LendTransactionStatus.INITIALIZED_LABEL_CODE;
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.INITIALIZED_LABEL_CODE;
			}			
		}
		else if (LendTransactionStatus.OPENED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender must lend the object.
			if (isCurrentUserLender) {
				return LendTransactionStatus.OPENED_LABEL_CODE;
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.OPENED_LABEL_CODE;
			}			
		}
		else if (LendTransactionStatus.IN_PROGRESS_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender has nothing to do.
			if (isCurrentUserLender) {
				return LendTransactionStatus.IN_PROGRESS_LABEL_CODE;
			}
			// Borrower must give the object back.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.IN_PROGRESS_LABEL_CODE;
			}			
		}
		else if (LendTransactionStatus.WAITING_EVALUATION_FROM_BOTH_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender must evaluate.
			if (isCurrentUserLender) {
				return LendTransactionStatus.WAITING_EVALUATION_FROM_BOTH_LABEL_CODE;
			}
			// Borrower must evaluate.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.WAITING_EVALUATION_FROM_BOTH_LABEL_CODE;
			}			
		}
		else if (LendTransactionStatus.WAITING_EVALUATION_FROM_BORROWER_CODE.equals(status.getLabelCode())) {
			// Lender has nothing to do.
			if (isCurrentUserLender) {
				return LendTransactionStatus.WAITING_EVALUATION_FROM_BORROWER_CODE;
			}
			// Borrower must evaluate.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.WAITING_EVALUATION_FROM_BORROWER_CODE;
			}			
		}
		else if (LendTransactionStatus.WAITING_EVALUATION_FROM_LENDER_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender must evaluate.
			if (isCurrentUserLender) {
				return LendTransactionStatus.WAITING_EVALUATION_FROM_LENDER_LABEL_CODE;
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.WAITING_EVALUATION_FROM_LENDER_LABEL_CODE;
			}			
		}
		else if (LendTransactionStatus.COMPLETED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender has nothing to do.
			if (isCurrentUserLender) {
				return LendTransactionStatus.COMPLETED_LABEL_CODE;
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.COMPLETED_LABEL_CODE;
			}			
		}
		else if (LendTransactionStatus.CANCELED_LABEL_CODE.equals(status.getLabelCode())) {
			// Lender has nothing to do.
			if (isCurrentUserLender) {
				return LendTransactionStatus.CANCELED_LABEL_CODE;
			}
			// Borrower has nothing to do.
			else if (isCurrentUserBorrower) {
				return LendTransactionStatus.CANCELED_LABEL_CODE;
			}			
		}
		else {
			throw new RuntimeException("Unknown status: " + status.getLabelCode());
		}
		
		final Person currentPerson = personService.getCurrentPerson();
		if (currentPerson.getUser().isAdmin()) {
			return "Admins can just watch...";
		}
		else {
			throw new RuntimeException("User should not be here: " + currentPerson.getUser().getUsername());
		}
	}
}
