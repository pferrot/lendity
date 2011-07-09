package com.pferrot.lendity.lendtransaction.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.orchestra.viewController.annotations.InitView;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.lendtransaction.LendTransactionConsts;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractLendTransactionsListController extends AbstractListController {
	
	private LendTransactionService lendTransactionService;
	private ItemService itemService;
	
	private List<SelectItem> statusSelectItems;
	private Long statusId;
	
	public final static String FORCE_VIEW_PARAM_NAME = "view";
	public final static String FORCE_VIEW_UNCOMPLETED_LEND_TRANSACTIONS = "uncompleted";

	@InitView
	public void initView() {
		final String filterBy = JsfUtils.getRequestParameter(FORCE_VIEW_PARAM_NAME);
		if (FORCE_VIEW_UNCOMPLETED_LEND_TRANSACTIONS.equals(filterBy)) {
			setStatusId(LendTransactionConsts.UNCOMPLETED_STATUS_SELECT_ITEM_VALUE);			
		}
		else {
			setStatusId(null);
		}
	}
	
	public AbstractLendTransactionsListController() {
		super();
		setRowsPerPage(LendTransactionConsts.NB_LEND_TRANSACTIONS_PER_PAGE);
	}

	public LendTransactionService getLendTransactionService() {
		return lendTransactionService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setLendTransactionService(LendTransactionService lendTransactionService) {
		this.lendTransactionService = lendTransactionService;
	}
	
	public String getLendTransactionOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.LEND_TRANSACTION_OVERVIEW,
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_LEND_TRANSACTION_ID,
				lendTransaction.getId().toString());
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
	
	public boolean isInternalBorrower() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public String getLenderOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getLender().getId().toString());
	}

	public String getItemOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return ItemUtils.getItemOverviewPageUrl(lendTransaction.getItem().getId().toString());
	}
	
	public String getRequestDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return UiUtils.getDateAsString(lendTransaction.getLendRequest().getRequestDate(), I18nUtils.getDefaultLocale());
	}

	public String getStartDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getStartDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getStartDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}

	public String getEndDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getStartDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getEndDate(), I18nUtils.getDefaultLocale());
		}
		else {
			return null;
		}
	}
	
	public String getBorrowDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getItem().getBorrowDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getItem().getBorrowDate(), I18nUtils.getDefaultLocale());
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

	public List<SelectItem> getStatusSelectItems() {
		if (statusSelectItems == null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			statusSelectItems = UiUtils.getSelectItemsForOrderedListValue(getLendTransactionService().getStatuses(), locale);
			statusSelectItems.add(0, getUncompletedStatusSelectItem(locale));
			// Add all visibilities first.
			statusSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
			
		}		
		return statusSelectItems;	
	}
	
	private SelectItem getUncompletedStatusSelectItem(final Locale pLocale) {
		final String label = I18nUtils.getMessageResourceString("lendTransaction_statusUncompleted", pLocale);
		final SelectItem si = new SelectItem(LendTransactionConsts.UNCOMPLETED_STATUS_SELECT_ITEM_VALUE, label);
		return si;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(final Long pStatusId) {
		this.statusId = UiUtils.getPositiveLongOrNull(pStatusId);
	}

	public void status(final ValueChangeEvent pEevent) {
    	final Long status = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setStatusId(status);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }

	public String clearStatus() {
		setStatusId(null);
		return "clearStatus";
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

	public boolean isBorrowerHrefAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getBorrower() != null;
	}
	
	public boolean isStartDateAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getStartDate() != null;
	}
	
	public boolean isEndDateAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getEndDate() != null;
	}

	public String getImage1Src() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getItem() == null) {
			return null;
		}
		else {
			return getItemService().getItemPicture1Src(lendTransaction.getItem(), true);
		}
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

	@Override
	protected void resetFilters() {
		super.resetFilters();
		setStatusId(null);
	}

	public boolean isItemAvailable() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return lendTransaction.getItem() != null;
	}
}
