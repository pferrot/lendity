package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class MyItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyItemsListController.class);

	@Override
	public List getListInternal() {
		return getItemService().findItemsOwnedByPersonId(PersonUtils.getCurrentPersonId(), getFirstResultIndex(), ItemConsts.NB_ITEMS_PER_PAGE + 1);
	}
}
