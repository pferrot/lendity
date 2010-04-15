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
		return getItemService().findItems(PersonUtils.getCurrentPersonId(), getSearchString(), getCategoryId(), getFirstRow(), getRowsPerPage());
	}
	

	
}
