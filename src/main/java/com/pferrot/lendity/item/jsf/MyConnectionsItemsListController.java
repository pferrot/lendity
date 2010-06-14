package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.utils.UiUtils;

public class MyConnectionsItemsListController extends AbstractItemsWithOwnerListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsItemsListController.class);
	
	
	public MyConnectionsItemsListController() {
		super();
		// Display available items by default.
		setBorrowStatus(UiUtils.getLongFromBoolean(Boolean.FALSE));
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
}