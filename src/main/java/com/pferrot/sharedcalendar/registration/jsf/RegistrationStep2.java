package com.pferrot.sharedcalendar.registration.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.registration.RegistrationService;

@ViewController(viewIds={"/public/registration/registration_2.jspx"})
public class RegistrationStep2
// Renderable is NOT necessary in sync mode.
//implements Renderable, DisposableBean 
{
	private final static Log log = LogFactory.getLog(RegistrationStep2.class);
	
	private RegistrationController registrationController;
	private RegistrationService registrationService;
	
	public RegistrationStep2() {
		super();
	}
	
	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("registration", "/public/registration/registration.iface");
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
		try {
			// Must be done in the view controller so that we do not get a LazyInitializationException.
			// (RegistrationController is in the correct scope to not get the exception, see application-context.xml)
			getRegistrationController().createUser();
			
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
