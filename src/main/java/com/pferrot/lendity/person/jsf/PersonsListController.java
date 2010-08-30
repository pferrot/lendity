package com.pferrot.lendity.person.jsf;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.utils.JsfUtils;

public class PersonsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(PersonsListController.class);
	
	private final static String REQUEST_CONNECTION_ATTRIUTE_PREFIX = "REQUEST_CONNECTION_AVAILABLE_";
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getPersonService().findEnabledPersons(getSearchString(), getFirstRow(), getRowsPerPage());
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
			
			// Not sure why this is called 3 times per person !? Avoid hitting DB.
			final HttpServletRequest request = JsfUtils.getRequest();
			final Boolean requestResult = (Boolean)request.getAttribute(REQUEST_CONNECTION_ATTRIUTE_PREFIX + person.getId());
			if (requestResult != null) {
				return requestResult.booleanValue();
			}
			boolean result = !getConnectionRequestService().isConnectionRequestAllowedFromCurrentUser(person);
			request.setAttribute(REQUEST_CONNECTION_ATTRIUTE_PREFIX + person.getId(), Boolean.valueOf(result));
			return result;			
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}			
	}
	
}
