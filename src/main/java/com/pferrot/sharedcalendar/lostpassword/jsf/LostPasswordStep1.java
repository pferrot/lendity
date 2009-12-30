package com.pferrot.sharedcalendar.lostpassword.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.lostpassword.LostPasswordService;

public class LostPasswordStep1 {
	
	private final static Log log = LogFactory.getLog(LostPasswordStep1.class);
	
	private LostPasswordController lostPasswordController;
	private LostPasswordService lostPasswordService;

	public LostPasswordStep1() {
		super();
	}
	
	public LostPasswordController getLostPasswordController() {
		return lostPasswordController;
	}

	public void setLostPasswordController(
			LostPasswordController lostPasswordController) {
		this.lostPasswordController = lostPasswordController;
	}

	public void setLostPasswordService(LostPasswordService lostPasswordService) {
		this.lostPasswordService = lostPasswordService;
	}	
	
	public String submit() {
		getLostPasswordController().sendPassword();
		return "success";
	}
	
	public void validateEmail(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String email = (String) value;
		if (!email.contains("@")) {
			((UIInput)toValidate).setValid(false);
			// TODO
			message = "Email not valid";
			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
		else if (!lostPasswordService.isUsernameExistingAndEnabled(email)) {
			((UIInput)toValidate).setValid(false);
			// TODO
			message = "No user found";
			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
}
