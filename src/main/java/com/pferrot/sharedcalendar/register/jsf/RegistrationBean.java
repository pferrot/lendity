package com.pferrot.sharedcalendar.register.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.pferrot.sharedcalendar.register.RegistrationService;

public class RegistrationBean {
	
	private RegistrationService registrationService;
	
	private String username;
	private String password;
	private String passwordRepeat;
	private String firstName;
	private String lastName;
	private String email;
	
	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String submit() {
		// Validation is done by methods below.
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
	}
	
	public void validateUsername(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String username = (String) value;
		if (!registrationService.isUsernameAvailable(username)) {
			((UIInput)toValidate).setValid(false);
			// TODO
			message = "Username already used";
			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
	// TODO: Does not work: getPassword() always returns null.
	public void validatePasswordRepeat(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String passwordRepeat = (String) value;
		if (!passwordRepeat.equals(getPassword())) {
			((UIInput)toValidate).setValid(false);
			// TODO
			message = "Confirmation password does not match password";
			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}	
	
	
	

}
