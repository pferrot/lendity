package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.person.PersonService;
import com.pferrot.sharedcalendar.utils.JsfUtils;

public abstract class AbstractPersonAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractPersonAddEditController.class);
	
	private PersonService personService;
	
	private String firstName;
	private String lastName;
	private String displayName;
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}	

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public abstract Long processPerson();
	
	public String submit() {
		Long personId = processPerson();
		
		JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, personId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}	
}
