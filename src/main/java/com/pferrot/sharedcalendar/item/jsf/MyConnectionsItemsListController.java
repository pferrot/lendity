package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemConsts;

public class MyConnectionsItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsItemsListController.class);
	
	@Override
	public List getListInternal() {
		return getItemService().findVisibleItemsOwnedByCurrentPersonConnections(getFirstResultIndex(), ItemConsts.NB_ITEMS_PER_PAGE + 1);
	}
}