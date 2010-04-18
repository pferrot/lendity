package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class MyLentItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyLentItemsListController.class);

	@Override
	public List getListInternal() {
		return null;
//		return getItemService().findItemsLentByPersonId(PersonUtils.getCurrentPersonId(), getFirstResultIndex(), ItemConsts.NB_ITEMS_PER_PAGE + 1);
	}
}
