package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class MyBannedPersonsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(MyBannedPersonsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getPersonService().findBannedPersons(PersonUtils.getCurrentPersonId(), getSearchString(), getFirstRow(), getRowsPerPage());
	}
}
