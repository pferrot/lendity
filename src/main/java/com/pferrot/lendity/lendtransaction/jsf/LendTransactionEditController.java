package com.pferrot.lendity.lendtransaction.jsf;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;
import org.springframework.security.AccessDeniedException;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nConsts;
import com.pferrot.lendity.lendtransaction.exception.LendTransactionException;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/lendtransaction/lendTransactionEdit.jspx"})
public class LendTransactionEditController extends AbstractLendTransactionOverviewEditController
{
	private final static Log log = LogFactory.getLog(LendTransactionEditController.class);
	
	private Date startDate;
	private Date endDate;
	
	@InitView
	public void initView() {
		// Read the person ID from the request parameter and load the correct person.
		try {
			final String idString = JsfUtils.getRequestParameter(PagesURL.LEND_TRANSACTION_EDIT_PARAM_LEND_TRANSACTION_ID);
			LendTransaction lendTransaction = null;
			if (idString != null) {
				setLendTransactionId(Long.parseLong(idString));
				lendTransaction = getLendTransactionService().findLendTransaction(getLendTransactionId());
				// Access control check.
				if (!getLendTransactionService().isCurrentUserAuthorizedToEdit(lendTransaction)) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (lend transaction edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), lendTransaction = " + idString);
					}
					return;
				}
				else {
					setLendTransaction(lendTransaction);
				}
			}
			if (getLendTransaction() == null) {
				JsfUtils.redirect(PagesURL.MY_LEND_TRANSACTIONS_LIST);
				return;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDateAsString(final String pStartDateAsString) {
		try {
			if (StringUtils.isNullOrEmpty(pStartDateAsString)) {
				setStartDate(null);
			}
			else {
				setStartDate(I18nConsts.DATE_FORMAT.parse(pStartDateAsString));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getStartDateAsString() {
		if (getStartDate() == null) {
			return "";
		}
		else {
			return I18nConsts.DATE_FORMAT.format(getStartDate());
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setEndDateAsString(final String pEndDateAsString) {
		try {
			if (StringUtils.isNullOrEmpty(pEndDateAsString)) {
				setEndDate(null);
			}
			else {
				setEndDate(I18nConsts.DATE_FORMAT.parse(pEndDateAsString));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getEndDateAsString() {
		if (getEndDate() == null) {
			return "";
		}
		else {
			return I18nConsts.DATE_FORMAT.format(getEndDate());
		}
	}

	public boolean isEditStartDateAvailable() {
		final String statusCode = getLendTransaction().getStatus().getLabelCode();
		return LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(statusCode) || 
			LendTransactionStatus.OPENED_LABEL_CODE.equals(statusCode); 
	}

	public boolean isEditEndDateAvailable() {
		final String statusCode = getLendTransaction().getStatus().getLabelCode();
		return LendTransactionStatus.INITIALIZED_LABEL_CODE.equals(statusCode) || 
			LendTransactionStatus.OPENED_LABEL_CODE.equals(statusCode) || 
			LendTransactionStatus.IN_PROGRESS_LABEL_CODE.equals(statusCode); 
	}

	@Override
	public void setLendTransaction(final LendTransaction pLendTransaction) {
		super.setLendTransaction(pLendTransaction);
		
		setStartDate(pLendTransaction.getStartDate());
		setEndDate(pLendTransaction.getEndDate());
	}	

	public Long updateLendTransaction() {		
		try {
			getLendTransactionWithCommentService().updateLendTransactionDates(getLendTransaction(), getStartDate(), getEndDate());
			
			return getLendTransaction().getId();
		}
		catch (LendTransactionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String submit() {
		Long lendTransactionId = updateLendTransaction();
		
		JsfUtils.redirect(PagesURL.LEND_TRANSACTION_OVERVIEW, PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID, lendTransactionId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}
	
	public String getLendTransactionOverviewHref() {
		return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_OVERVIEW, PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID, getLendTransaction().getId().toString());
	}
}
