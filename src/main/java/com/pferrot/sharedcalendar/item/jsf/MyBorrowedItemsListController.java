package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;

public class MyBorrowedItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyBorrowedItemsListController.class);

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return null;
//		return getItemService().findConnectionsItems(PersonUtils.getCurrentPersonId(), getSearchString(), getCategoryId(), 
//				getVisibleStatusBoolean(), getBorrowStatusBoolean(), getFirstRow(), getRowsPerPage());
	}
}
