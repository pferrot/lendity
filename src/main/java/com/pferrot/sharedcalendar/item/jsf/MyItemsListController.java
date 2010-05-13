package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;

public class MyItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyItemsListController.class);
	
	public MyItemsListController() {
		super();
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findMyItems(getSearchString(), getCategoryId(), 
				getVisibleStatusBoolean(), getBorrowStatusBoolean(), getFirstRow(), getRowsPerPage());
	}
	

	
}
