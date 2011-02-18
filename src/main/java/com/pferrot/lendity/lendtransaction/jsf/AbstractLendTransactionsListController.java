package com.pferrot.lendity.lendtransaction.jsf;

import javax.faces.context.FacesContext;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.lendtransaction.LendTransactionConsts;
import com.pferrot.lendity.lendtransaction.LendTransactionService;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractLendTransactionsListController extends AbstractListController {
	
	private LendTransactionService lendTransactionService;
	private ItemService itemService;

	
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
				PagesURL.LEND_TRANSACTION_OVERVIEW_PARAM_NEED_ID,
				lendTransaction.getId().toString());
	}

	public String getBorrowerOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getBorrower().getId().toString());
	}
	
	public String getLenderOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(lendTransaction.getLender().getId().toString());
	}

	public String getItemOverviewHref() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return ItemUtils.getInternalItemOverviewPageUrl(lendTransaction.getItem().getId().toString());
	}
	
	public String getRequestDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return UiUtils.getDateAsString(lendTransaction.getLendRequest().getRequestDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}

	public String getStartDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getStartDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getStartDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		else {
			return null;
		}
	}

	public String getEndDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getStartDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getEndDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		else {
			return null;
		}
	}
	
	public String getBorrowDateLabel() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		if (lendTransaction.getItem().getBorrowDate() != null) {
			return UiUtils.getDateAsString(lendTransaction.getItem().getBorrowDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		else {
			return null;
		}
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
		return getItemService().getItemPicture1Src(lendTransaction.getItem(), true);
	}
	
	public String getThumbnail1Src() {
		final LendTransaction lendTransaction = (LendTransaction)getTable().getRowData();
		return getItemService().getItemThumbnail1Src(lendTransaction.getItem(), true);
	}
}
