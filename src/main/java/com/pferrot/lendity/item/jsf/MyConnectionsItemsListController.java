package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.lendrequest.exception.LendRequestException;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.utils.UiUtils;

public class MyConnectionsItemsListController extends AbstractItemsWithOwnerListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsItemsListController.class);
	
	private LendRequestService lendRequestService;	
	
	public MyConnectionsItemsListController() {
		super();
		// Display available items by default.
		setBorrowStatus(UiUtils.getLongFromBoolean(Boolean.FALSE));
	}
	
	public void setLendRequestService(final LendRequestService pLendRequestService) {
		this.lendRequestService = pLendRequestService;
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findMyConnectionsItems(getOwnerId(), getSearchString(), getCategoryId(), 
				getBorrowStatusBoolean(), getFirstRow(), getRowsPerPage());
	}

	@Override
	public List<SelectItem> getBorrowStatusSelectItems() {
		if (getBorrowStatusSelectItemsInternal() == null) {
			final List result = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			
			result.add(new SelectItem(UiUtils.getLongFromBoolean(null), I18nUtils.getMessageResourceString("item_availableStatusAll", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.FALSE), I18nUtils.getMessageResourceString("item_availableStatusAvailable", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.TRUE), I18nUtils.getMessageResourceString("item_availableStatusNotAvailable", locale)));
			
			setBorrowStatusSelectItemsInternal(result);
		}		
		return getBorrowStatusSelectItemsInternal();	
	}
	
	public boolean isRequestLendAvailable() {
		final InternalItem item = (InternalItem)getTable().getRowData();
		
		return lendRequestService.isLendRequestAllowedFromCurrentUser(item);	
	}
	
	public String requestLend() {
		try {
			final InternalItem item = (InternalItem)getTable().getRowData();
			lendRequestService.createLendRequestFromCurrentUser(item);
			return "requestLend";
		}
		catch (LendRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}
	}
}