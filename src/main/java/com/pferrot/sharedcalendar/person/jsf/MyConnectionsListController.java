package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.person.exception.PersonException;

public class MyConnectionsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getPersonService().findConnections(PersonUtils.getCurrentPersonId(), getSearchString(), getFirstRow(), getRowsPerPage());
	}

	public String removeConnection() {
		try {
			final Person person = (Person)getTable().getRowData();
			getPersonService().updateRemoveConnection(PersonUtils.getCurrentPersonId(), person.getId());
			return "removeConnection";
		}
		catch (PersonException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}
}
