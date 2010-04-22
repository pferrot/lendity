package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.connectionrequest.ConnectionRequestService;
import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonConsts;
import com.pferrot.sharedcalendar.person.PersonService;
import com.pferrot.sharedcalendar.person.PersonUtils;

public abstract class AbstractPersonsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractPersonsListController.class);
	
	private PersonService personService;
	private ConnectionRequestService connectionRequestService;

	public AbstractPersonsListController() {
		super();
		setRowsPerPage(PersonConsts.NB_PERSONS_PER_PAGE);
	}

	public void setPersonService(final PersonService pPersonService) {
		this.personService = pPersonService;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(final ConnectionRequestService pConnectionRequestService) {
		this.connectionRequestService = pConnectionRequestService;
	}

	public String getPersonOverviewHref() {
		final Person person = (Person)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString());
	}
}
