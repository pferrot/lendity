package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.person.exception.PersonException;

public class MyBannedPersonsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(MyBannedPersonsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getPersonService().findBannedPersons(PersonUtils.getCurrentPersonId(), getSearchString(), getFirstRow(), getRowsPerPage());
	}

	public String unbanPerson() {
		try {
			final Person person = (Person)getTable().getRowData();
			getPersonService().updateUnbanPerson(PersonUtils.getCurrentPersonId(), person.getId());
			return "unbanPerson";
		}
		catch (PersonException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}
}
