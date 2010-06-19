package com.pferrot.lendity.person.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

public class RemoveConnectionTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RemoveConnectionTooltipController.class);
	
	private PersonService personService;
	
	private Long personId;

	private Long redirectId;
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		try {
			getPersonService().updateRemoveConnection(PersonUtils.getCurrentPersonId(), getPersonId());
			
			JsfUtils.redirect(PagesURL.MY_CONNECTIONS_LIST);
		
			// As a redirect is used, this is actually useless.
			return null;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}	
}
