package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.security.SecurityUtils;
import com.pferrot.sharedcalendar.item.ItemConsts;

public class MyItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyItemsListController.class);

	@Override
	public List getListInternal() {
		return getItemService().findItemsOwnedByUsername(SecurityUtils.getCurrentUsername(), getFirstResultIndex(), ItemConsts.NB_ITEMS_PER_PAGE + 1);
	}
}
