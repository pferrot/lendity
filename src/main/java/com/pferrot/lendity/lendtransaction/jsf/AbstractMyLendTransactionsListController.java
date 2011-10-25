package com.pferrot.lendity.lendtransaction.jsf;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractMyLendTransactionsListController implements Serializable {

	private final static Log log = LogFactory.getLog(AbstractMyLendTransactionsListController.class);
	
	private final static String IN_PROGRESS_LIST_LOADED_ATTRIBUTE_NAME = "inProgressListLoaded";
	private final static String FUTURE_LIST_LOADED_ATTRIBUTE_NAME = "futureListLoaded";
	private final static String TO_EVALUATE_LIST_LOADED_ATTRIBUTE_NAME = "toEvaluateListLoaded";
	
	protected final static String LIST_LOADED_ATTRIBUTE_VALUE = "true";
	
	private LendTransactionService lendTransactionService;
	private ItemService itemService;
	
	private List inProgressList;
	private List futureList;
	private List toEvaluateList;
	
	private HtmlDataTable inProgressTable;
	private HtmlDataTable futureTable;
	private HtmlDataTable toEvaluateTable;
	
	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public void setLendTransactionService(
			LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public List getInProgressList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(IN_PROGRESS_LIST_LOADED_ATTRIBUTE_NAME))) {
				inProgressList =  getInProgressListInternal();
				request.setAttribute(IN_PROGRESS_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return inProgressList;
	}
	
	protected abstract List getInProgressListInternal();
	
	public List getFutureList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(FUTURE_LIST_LOADED_ATTRIBUTE_NAME))) {
				futureList =  getFutureListInternal();
				request.setAttribute(FUTURE_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return futureList;
	}
	
	protected abstract List getFutureListInternal();
	
	public List getToEvaluateList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(TO_EVALUATE_LIST_LOADED_ATTRIBUTE_NAME))) {
				toEvaluateList =  getToEvaluateListInternal();
				request.setAttribute(TO_EVALUATE_LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return toEvaluateList;
	}

	public abstract List getToEvaluateListInternal();	
		
	public HtmlDataTable getInProgressTable() {
		return inProgressTable;
	}

	public void setInProgressTable(HtmlDataTable inProgressTable) {
		this.inProgressTable = inProgressTable;
	}

	public HtmlDataTable getFutureTable() {
		return futureTable;
	}

	public void setFutureTable(HtmlDataTable futureTable) {
		this.futureTable = futureTable;
	}

	public HtmlDataTable getToEvaluateTable() {
		return toEvaluateTable;
	}

	public void setToEvaluateTable(HtmlDataTable toEvaluateTable) {
		this.toEvaluateTable = toEvaluateTable;
	}
	
	public String getInProgressThumbnail1Src() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		if (lendTransaction.getItem() == null) {
			return null;
		}
		else {
			return getItemService().getThumbnail1Src(lendTransaction.getItem(), true);
		}
	}
	
	public String getFutureThumbnail1Src() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		if (lendTransaction.getItem() == null) {
			return null;
		}
		else {
			return getItemService().getThumbnail1Src(lendTransaction.getItem(), true);
		}
	}
	
	public String getToEvaluateThumbnail1Src() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		if (lendTransaction.getItem() == null) {
			return null;
		}
		else {
			return getItemService().getThumbnail1Src(lendTransaction.getItem(), true);
		}
	}
	
	public String getInProgressItemOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		return ItemUtils.getItemOverviewPageUrl(lendTransaction.getItem().getId().toString());
	}
	
	public String getFutureItemOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		return ItemUtils.getItemOverviewPageUrl(lendTransaction.getItem().getId().toString());
	}
	
	public String getToEvaluateItemOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		return ItemUtils.getItemOverviewPageUrl(lendTransaction.getItem().getId().toString());
	}
	
	public boolean isInProgressItemAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		return lendTransaction.getItem() != null;
	}
	
	public boolean isFutureItemAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		return lendTransaction.getItem() != null;
	}
	
	public boolean isToEvaluateItemAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		return lendTransaction.getItem() != null;
	}
	
	public boolean isInProgressInternalBorrower() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public boolean isFutureInternalBorrower() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public boolean isToEvaluateInternalBorrower() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public String getInProgressLenderOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getLender().getId().toString());
	}
	
	public String getFutureLenderOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getLender().getId().toString());
	}
	
	public String getToEvaluateLenderOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getLender().getId().toString());
	}
	
	public boolean isInProgressBorrowerHrefAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public boolean isFutureBorrowerHrefAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public boolean isToEvaluateBorrowerHrefAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public String getInProgressBorrowerOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		if (lendTransaction.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getBorrower().getId().toString());
		}
		else {
			return null;
		}
	}
	
	public String getFutureBorrowerOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		if (lendTransaction.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getBorrower().getId().toString());
		}
		else {
			return null;
		}
	}
	
	public String getToEvaluateBorrowerOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		if (lendTransaction.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getBorrower().getId().toString());
		}
		else {
			return null;
		}
	}
	
	public String getInProgressStatusLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		final LendTransactionStatus status = lendTransaction.getStatus();
		if (status != null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString(status.getLabelCode(), locale);	
		}
		else {
			return null;
		}
	}
	
	public String getFutureStatusLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		final LendTransactionStatus status = lendTransaction.getStatus();
		if (status != null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString(status.getLabelCode(), locale);	
		}
		else {
			return null;
		}
	}
	
	public String getToEvaluateStatusLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		final LendTransactionStatus status = lendTransaction.getStatus();
		if (status != null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString(status.getLabelCode(), locale);	
		}
		else {
			return null;
		}
	}
	
	public String getInProgressCreationDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getCreationDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getInProgressEndDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getEndDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getFutureCreationDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getCreationDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getFutureStartDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getStartDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getToEvaluateCreationDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getCreationDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getInProgressLendTransactionOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getInProgressTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lendTransaction.getId().toString());
	}
	
	public String getFutureLendTransactionOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getFutureTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lendTransaction.getId().toString());
	}
	
	public String getToEvaluateLendTransactionOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getToEvaluateTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lendTransaction.getId().toString());
	}
}
