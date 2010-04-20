package com.pferrot.sharedcalendar.registration.jsf;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.registration.RegistrationConsts;
import com.pferrot.sharedcalendar.registration.RegistrationService;

public class RegistrationStep1 {
	
	private final static Log log = LogFactory.getLog(RegistrationStep1.class);
	
	private RegistrationController registrationController;
	private RegistrationService registrationService;
	
	public RegistrationStep1() {
		super();
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

	public String submit() {
		if (log.isDebugEnabled()) {
			log.debug("Clicked submit");
		}
		// Validation is done by methods below.
		return "success";
	}
	
	public void validateEmail(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String email = (String) value;
		if (!email.contains("@")) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_emailNotValid", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
		else if (!registrationService.isUsernameAvailable(email)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_userAlreadyExists", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}

	public void validatePassword(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String password = (String) value;
		if (password.length() < 4) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_passwordMinSize", new Object[]{String.valueOf(RegistrationConsts.MIN_PASSWORD_SIZE)}, locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}

	public void validatePasswordRepeat(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String passwordRepeat = (String) value;
		
		final UIComponent passwordComponent = toValidate.findComponent("password");
		final EditableValueHolder passwordEditableValueHolder = (EditableValueHolder)passwordComponent;
		final String password = (String)passwordEditableValueHolder.getValue();

		if (!passwordRepeat.equals(password)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_passwordsNotMatch", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
	// TODO: Does not work: getPassword() always returns null.
//	public void validatePasswordRepeat(FacesContext context, UIComponent toValidate, Object value) {
//		String message = "";
//		String passwordRepeat = (String) value;
//		if (!passwordRepeat.equals(getPassword())) {
//			((UIInput)toValidate).setValid(false);
//			// TODO
//			message = "Confirmation password does not match password";
//			//message = CoffeeBreakBean.loadErrorMessage(context, CoffeeBreakBean.CB_RESOURCE_BUNDLE_NAME, "EMailError");
//			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
//		}
//	}	

//	public PersistentFacesState getState() {
//		return state;
//	}

//	public void renderingException(RenderingException renderingException) {
//		if (log. isDebugEnabled()) {			 
//			log.debug("Rendering exception:  " + renderingException);
//		}	 
//		if (renderingException instanceof FatalRenderingException) { 
//			performCleanup();	 
//		}		
//	}

//	protected boolean performCleanup() {
//		try {
//			if (clock != null) {	
//				clock.remove(this);
//				if (clock.isEmpty() ) { 
//					clock.dispose();
//				}
//				clock = null;
//			}
//			return true;
//		} 
//		catch (Exception failedCleanup) {
//			if (log.isErrorEnabled()) {
//				log.error("Failed to cleanup a clock bean", failedCleanup);
//			}
//		}
//		return false;
//	}
	
//	public void setRenderManager(RenderManager renderManager) {
//		clock = renderManager.getIntervalRenderer("clock"); 
//		clock.setInterval(renderInterval); 
//		clock.add(this);  
//		clock.requestRender();
//	}

//	public void dispose() throws Exception {
//        if (log.isInfoEnabled()) {        	 
//            log.info("Dispose RegistrationBean for a user - cleaning up"); 
//        } 
//        performCleanup();		
//	}

}
