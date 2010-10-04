package com.pferrot.lendity.registration.jsf;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.registration.RegistrationConsts;
import com.pferrot.lendity.registration.RegistrationService;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/registration/registration.jspx"})
public class RegistrationStep1 {
	
	private final static Log log = LogFactory.getLog(RegistrationStep1.class);
	
	private RegistrationController registrationController;
	private RegistrationService registrationService;
	
	public RegistrationStep1() {
		super();
	}
	
	@InitView
	public void initView() {
		final String email = JsfUtils.getRequestParameter(RegistrationConsts.REGISTRATION_EMAIL_PARAM_NAME);
		if (!StringUtils.isNullOrEmpty(email)) {
			getRegistrationController().setEmail(email.trim());
		}
		final String code = JsfUtils.getRequestParameter(RegistrationConsts.REGISTRATION_CODE_PARAM_NAME);
		if (!StringUtils.isNullOrEmpty(code)) {
			getRegistrationController().setBetaCode(code.trim());
		}
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
		if (password == null || !password.matches(RegistrationConsts.PASSWORD_REGEXP)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_passwordConstraints", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
	public void validateCaptcha(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String captchaUserValue = (String) value;
		String captchaCorrectValue = (String)((HttpSession) FacesContext.getCurrentInstance().
				getExternalContext().getSession(true)).getAttribute(getRegistrationController().getCaptchaSessionKeyName());
		if (! captchaCorrectValue.equalsIgnoreCase(captchaUserValue)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_captchaWrong", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
	public void validateBetaCode(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		final String betaCodeUserValue = (String) value;

		// Get the email.
		final UIComponent emailComponent = toValidate.findComponent("email");
		final EditableValueHolder emailEditableValueHolder = (EditableValueHolder)emailComponent;
		final String email = (String)emailEditableValueHolder.getValue();
		
		String betaCodeCorrectValue = null;
		if (!StringUtils.isNullOrEmpty(email)) {
			betaCodeCorrectValue = getRegistrationService().getBetaCodeCorrectValue(email);
		}		 
		if (betaCodeCorrectValue == null ||
			! betaCodeCorrectValue.equals(betaCodeUserValue)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_betaCodeWrong", locale);
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

	public void validateTermsAndConditions(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		Boolean termsAndConditionsAccepted = (Boolean) value;
		
		if (termsAndConditionsAccepted == null ||
			!termsAndConditionsAccepted.booleanValue()) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_termsAndConditionsNotAccepted", locale);
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
