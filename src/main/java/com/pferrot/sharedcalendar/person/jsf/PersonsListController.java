package com.pferrot.sharedcalendar.person.jsf;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
}
