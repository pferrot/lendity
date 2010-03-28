package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class MyBorrowedItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyBorrowedItemsListController.class);

	@Override
	public List getListInternal() {
		return getItemService().findItemsBorrowedByPersonId(PersonUtils.getCurrentPersonId(), getFirstResultIndex(), ItemConsts.NB_ITEMS_PER_PAGE + 1);
	}
}
