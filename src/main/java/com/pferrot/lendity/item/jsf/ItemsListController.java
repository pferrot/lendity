package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class ItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(ItemsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return null;
//		return getItemService().findConnectionsItems(PersonUtils.getCurrentPersonId(), getSearchString(), getCategoryId(), 
//				getVisibleStatusBoolean(), getBorrowStatusBoolean(), getFirstRow(), getRowsPerPage());
	}
}
