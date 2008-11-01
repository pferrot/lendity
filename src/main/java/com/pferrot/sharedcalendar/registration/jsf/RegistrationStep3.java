package com.pferrot.sharedcalendar.registration.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.registration.RegistrationService;

public class RegistrationStep3
// Renderable is NOT necessary in sync mode.
//implements Renderable, DisposableBean 
{
	
	
	private final static Log log = LogFactory.getLog(RegistrationStep3.class);
	
	private RegistrationViewController registrationViewController;
	private RegistrationService registrationService;

	
	
	public RegistrationStep3() {
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
		// Validation is done by methods below.
		return "confirm";
	}
	
	public String back() {
		// Validation is done by methods below.
		return "back";
	}	

}
