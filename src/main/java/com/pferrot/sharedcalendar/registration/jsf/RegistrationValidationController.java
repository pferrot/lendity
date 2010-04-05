package com.pferrot.sharedcalendar.registration.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.registration.RegistrationConsts;
import com.pferrot.sharedcalendar.registration.RegistrationService;
import com.pferrot.sharedcalendar.utils.JsfUtils;

@ViewController(viewIds={"/public/registration/registrationValidation.jspx"})
public class RegistrationValidationController {
	
	private final static Log log = LogFactory.getLog(RegistrationValidationController.class);
	
	private RegistrationService registrationService;
	private String username;
	private String activationCode;
	private boolean validationSuccessful = false; 	

	@InitView
	public void initView() {
		try {
			username = JsfUtils.getRequestParameter(RegistrationConsts.USERNAME_PARAMETER_NAME);
			activationCode = JsfUtils.getRequestParameter(RegistrationConsts.ACTIVATION_CODE_PARAMETER_NAME);
			getRegistrationService().updateUserValidation(username, activationCode);
			validationSuccessful = true;
		} catch (Exception e) {
			validationSuccessful = false;
		}
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public boolean isValidationSuccessful() {
		return validationSuccessful;
	}

	public void setValidationSuccessful(boolean validationSuccessful) {
		this.validationSuccessful = validationSuccessful;
	}
	
}
