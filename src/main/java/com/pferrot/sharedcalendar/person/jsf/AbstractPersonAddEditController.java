package com.pferrot.sharedcalendar.person.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.person.PersonService;
import com.pferrot.sharedcalendar.utils.JsfUtils;

public abstract class AbstractPersonAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractPersonAddEditController.class);
	
	private PersonService personService;
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public abstract Long processPerson();
	
	public String submit() {
		try {
			Long personId = processPerson();
			
			JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, personId.toString());
		
			// As a redirect is used, this is actually useless.
			return null;
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return "error";
		}
	}	
}
