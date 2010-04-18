package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemConsts;

public class ItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(ItemsListController.class);
	
	@Override
	public List getListInternal() {		
		return null;
//		// Is there a search string specified?
//		if (getSearchString() != null  && getSearchString().trim().length() > 0) {
//			// + 1 so that we can know whether there is a next page or not.
//			return getItemService().findItemsByTitle(getSearchString(), getFirstResultIndex(), ItemConsts.NB_ITEMS_PER_PAGE + 1);
//		}
//		else {
//			// + 1 so that we can know whether there is a next page or not.
//			return getItemService().findItems(getFirstResultIndex(), ItemConsts.NB_ITEMS_PER_PAGE + 1);
//		}
	}
}
