package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class MyItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(MyItemsListController.class);

	@Override
	public List getListInternal() {
		return null;
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		// Is there a search string specified?
		if (getSearchString() != null  && getSearchString().trim().length() > 0) {
			// + 1 so that we can know whether there is a next page or not.
			return getItemService().findItemsByTitleOwnedByPersonId(getSearchString(), PersonUtils.getCurrentPersonId(), getFirstRow(), getRowsPerPage());
		}
		else {
			// + 1 so that we can know whether there is a next page or not.
			return getItemService().findItemsOwnedByPersonId(PersonUtils.getCurrentPersonId(), getFirstRow(), getRowsPerPage());
		}
	}
	

	
}
