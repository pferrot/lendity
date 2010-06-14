package com.pferrot.lendity.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Person;

public class PersonsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(PersonsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		// Is there a search string specified?
		if (getSearchString() != null  && getSearchString().trim().length() > 0) {
			return getPersonService().findEnabledPersons(getSearchString(), getFirstRow(), getRowsPerPage());
		}
		else {
			return ListWithRowCount.emptyListWithRowCount();
		}
	}

	public String requestConnection() {
		try {
			final Person person = (Person)getTable().getRowData();
			getConnectionRequestService().createConnectionRequestFromCurrentUser(person);
			return "requestConnection";
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public boolean isRequestConnectionDisabled() {
		try {
			final Person person = (Person)getTable().getRowData();
			return !getConnectionRequestService().isConnectionRequestAllowedFromCurrentUser(person);
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}			
	}
	
}
