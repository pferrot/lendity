package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonUtils;

public class PersonAddController extends AbstractPersonAddEditController {
	
	private final static Log log = LogFactory.getLog(PersonAddController.class);

	public Long createPerson() {
		Person person = new Person();
		
		// TODO: what to set?
				
		return getPersonService().createPerson(person);		
	}

	public String getPersonsListHref() {		
		return PersonUtils.getPersonsListUrl();
	}
	
	@Override
	public Long processPerson() {
		return createPerson();
	}
}
