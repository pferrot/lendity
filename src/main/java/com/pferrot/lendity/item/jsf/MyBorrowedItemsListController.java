package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyBorrowedItemsListController extends AbstractItemsWithOwnerListController {
	
	private final static Log log = LogFactory.getLog(MyBorrowedItemsListController.class);

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getItemService().findMyBorrowedItems(getOwnerId(), getSearchString(), getCategoryId(), 
						getFirstRow(), getRowsPerPage());
	}
}
