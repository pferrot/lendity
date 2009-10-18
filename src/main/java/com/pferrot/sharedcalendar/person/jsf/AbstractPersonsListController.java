package com.pferrot.sharedcalendar.person.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonConsts;
import com.pferrot.sharedcalendar.person.PersonService;
import com.pferrot.sharedcalendar.person.PersonUtils;

public abstract class AbstractPersonsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractPersonsListController.class);
	
	private PersonService personService;
	
	public void setPersonService(final PersonService pPersonService) {
		this.personService = pPersonService;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	@Override
	public int getNbEntriesPerPage() {
		return PersonConsts.NB_PERSONS_PER_PAGE;
	}

	public String getPersonOverviewHref() {
		final Person person = (Person)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(person.getId().toString());
	}
}
