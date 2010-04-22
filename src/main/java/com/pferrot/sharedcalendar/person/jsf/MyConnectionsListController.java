package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class MyConnectionsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getPersonService().findConnections(PersonUtils.getCurrentPersonId(), getSearchString(), getFirstRow(), getRowsPerPage());
	}
}
