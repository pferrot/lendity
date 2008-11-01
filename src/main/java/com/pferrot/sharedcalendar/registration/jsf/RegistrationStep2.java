package com.pferrot.sharedcalendar.registration.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.registration.RegistrationService;

public class RegistrationStep2
// Renderable is NOT necessary in sync mode.
//implements Renderable, DisposableBean 
{
	
	
	private final static Log log = LogFactory.getLog(RegistrationStep2.class);
	
	private RegistrationViewController registrationViewController;
	private RegistrationService registrationService;

	
	
	public RegistrationStep2() {
		super();
	}
	
	public RegistrationViewController getRegistrationViewController() {
		return registrationViewController;
	}

	public void setRegistrationViewController(
			RegistrationViewController registrationViewController) {
		this.registrationViewController = registrationViewController;
	}

	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public String confirm() {
		try {
			// Must be done in the view controller so that we do not get a LazyInitializationException.
			// (RegistrationViewController is in the correct scope to not get the exception, see application-context.xml)
			getRegistrationViewController().createUser();
			
			// Validation is done by methods below.
			return "confirm";
		}
		catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e);
			}
			return "error";
		}
	}
	
	public String back() {
		// Validation is done by methods below.
		return "back";
	}	

}
