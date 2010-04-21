package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class MyConnectionsItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsItemsListController.class);
	
	@Override
	public List getListInternal() {
		return null;
	}
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findConnectionsItems(PersonUtils.getCurrentPersonId(), getSearchString(), getCategoryId(), 
				getVisibleStatusBoolean(), getBorrowStatusBoolean(), getFirstRow(), getRowsPerPage());
	}
}