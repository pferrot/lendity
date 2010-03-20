package com.pferrot.sharedcalendar.person.jsf;

import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.security.SecurityUtils;
import com.pferrot.sharedcalendar.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class PersonsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(PersonsListController.class);
	
	@Override
	public List getListInternal() {		
		// Is there a search string specified?
		if (getSearchString() != null  && getSearchString().trim().length() > 0) {
			// + 1 so that we can know whether there is a next page or not.
			return getPersonService().findPersonsByAnything(getSearchString(), getFirstResultIndex(), getNbEntriesPerPage() + 1);
		}
		else {
			// TODO: return nothing if no search string - is that good? 
			return Collections.EMPTY_LIST;
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
	
	public String getRequestConnectionLabel() {
//		final Person person = (Person)getTable().getRowData();
		return "request connection";
	}

	public boolean getRequestConnectionDisabled() {
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
