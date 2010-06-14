package com.pferrot.lendity.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.person.exception.PersonException;

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
