package com.pferrot.lendity.registration.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.registration.RegistrationService;

@ViewController(viewIds={"/public/registration/registration_2.jspx"})
public class RegistrationStep2 {
	
	private final static Log log = LogFactory.getLog(RegistrationStep2.class);
	
	private RegistrationController registrationController;
	private RegistrationService registrationService;
	
	public RegistrationStep2() {
		super();
	}
	
	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("registration", "/public/registration/registration.faces");
	}	
	
	public RegistrationController getRegistrationController() {
		return registrationController;
	}

	public void setRegistrationController(
			RegistrationController registrationController) {
		this.registrationController = registrationController;
	}

	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public String confirm() {
		// Must be done in the view controller so that we do not get a LazyInitializationException.
		// (RegistrationController is in the correct scope to not get the exception, see application-context.xml)
		getRegistrationController().createUser();
		
		// Validation is done by methods below.
		return "confirm";
	}
	
	public String back() {
		// Validation is done by methods below.
		return "back";
	}	

}
