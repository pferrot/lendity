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

public abstract class AbstractMyLendTransactionsWaitingForInputListController implements Serializable {

	private final static Log log = LogFactory.getLog(AbstractMyLendTransactionsWaitingForInputListController.class);
	
	private final static String LIST_LOADED_ATTRIBUTE_NAME = "listLoaded";
	
	protected final static String LIST_LOADED_ATTRIBUTE_VALUE = "true";
	
	private LendTransactionService lendTransactionService;
	private ItemService itemService;
	
	private List list;
	
	private HtmlDataTable table;
	
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

	public List getList() {		
		HttpServletRequest request = JsfUtils.getRequest();
		if (FacesContext.getCurrentInstance().getRenderResponse()
			&& ! LIST_LOADED_ATTRIBUTE_VALUE.equals(
					request.getAttribute(LIST_LOADED_ATTRIBUTE_NAME))) {
				list =  getListInternal();
				request.setAttribute(LIST_LOADED_ATTRIBUTE_NAME, LIST_LOADED_ATTRIBUTE_VALUE);
		}
        return list;
	}
	
	protected abstract List getListInternal();
		
	public HtmlDataTable getTable() {
		return table;
	}

	public void setTable(HtmlDataTable table) {
		this.table = table;
	}
	
	public String getThumbnail1Src() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getItem() == null) {
			return null;
		}
		else {
			return getItemService().getThumbnail1Src(lendTransaction.getItem(), true);
		}
	}
	
	public String getItemOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return ItemUtils.getItemOverviewPageUrl(lendTransaction.getItem().getId().toString());
	}
	
	public boolean isItemAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getItem() != null;
	}
	
	public boolean isInternalBorrower() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public String getLenderOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getLender().getId().toString());
	}
	
	public boolean isBorrowerHrefAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public String getBorrowerOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getBorrower().getId().toString());
		}
		else {
			return null;
		}
	}
	
	public String getStatusLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		final LendTransactionStatus status = lendTransaction.getStatus();
		if (status != null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			return I18nUtils.getMessageResourceString(status.getLabelCode(), locale);	
		}
		else {
			return null;
		}
	}
	
	public String getCreationDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getCreationDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getStartDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getStartDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getEndDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getCreationDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getEndDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getLendTransactionOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lendTransaction.getId().toString());
	}
}
