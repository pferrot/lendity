package com.pferrot.lendity.lendtransaction.jsf;

import java.util.Date;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.lendtransaction.LendTransactionWithCommentService;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.UiUtils;

public class AbstractLendTransactionOverviewEditController
{
	private final static Log log = LogFactory.getLog(AbstractLendTransactionOverviewEditController.class);
	
	private LendTransactionService lendTransactionService;
	private LendTransactionWithCommentService lendTransactionWithCommentService;
	private LendRequestService lendRequestService;
	private PersonService personService;
	private ItemService itemService;
	
	private Long lendTransactionId;
	private LendTransaction lendTransaction;
	
	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public LendTransactionWithCommentService getLendTransactionWithCommentService() {
		return lendTransactionWithCommentService;
	}

	public void setLendTransactionWithCommentService(
			LendTransactionWithCommentService lendTransactionWithCommentService) {
		this.lendTransactionWithCommentService = lendTransactionWithCommentService;
	}

	public LendRequestService getLendRequestService() {
		return lendRequestService;
	}

	public void setLendRequestService(LendRequestService lendRequestService) {
		this.lendRequestService = lendRequestService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
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
		final Locale locale = I18nUtils.getDefaultLocale();
		return I18nUtils.getMessageResourceString(getLendTransaction().getStatus().getLabelCode(), locale);		
	}

	public String getCreationDateLabel() {
		return UiUtils.getDateAsString(getLendTransaction().getCreationDate(), I18nUtils.getDefaultLocale());
	}
	
	public String getStartDateLabel() {
		final Date startDate = getLendTransaction().getStartDate();
		if (startDate != null) {
			return UiUtils.getDateAsString(startDate, I18nUtils.getDefaultLocale());
		}
		else {
			return "";
		}
	}

	public String getEndDateLabel() {
		final Date endDate = getLendTransaction().getEndDate();
		if (endDate != null) {
			return UiUtils.getDateAsString(endDate, I18nUtils.getDefaultLocale());
		}
		else {
			return "";
		}
	}

	public String getLendRequestText() {
		if (isLendRequestTextAvailable()) {
			return HtmlUtils.escapeHtmlAndReplaceCr(getLendTransaction().getLendRequest().getText());
		}
		return "";
	}

	public boolean isLendRequestTextAvailable() {
		return getLendTransaction() != null &&
			getLendTransaction().getLendRequest() != null &&
			! StringUtils.isNullOrEmpty(getLendTransaction().getLendRequest().getText());
	}

	public boolean isBorrowerHrefAvailable() {
		return getLendTransaction().getBorrower() != null;
	}
	
}
