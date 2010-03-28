package com.pferrot.sharedcalendar.person.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyConnectionsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsListController.class);
	
	@Override
	public List getListInternal() {		
		// Is there a search string specified?
		if (getSearchString() != null  && getSearchString().trim().length() > 0) {
			// + 1 so that we can know whether there is a next page or not.
			return getPersonService().findCurrentUserConnectionsByAnything(getSearchString(), getFirstResultIndex(), getNbEntriesPerPage() + 1);
		}
		else { 
			return getPersonService().findCurrentUserConnections(getFirstResultIndex(), getNbEntriesPerPage() + 1);
		}
	}	
}