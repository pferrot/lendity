package com.pferrot.lendity.changepassword.jsf;

import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.changepassword.ChangePasswordService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.registration.RegistrationConsts;
import com.pferrot.lendity.utils.JsfUtils;

public class ChangePasswordController {
	
	private final static Log log = LogFactory.getLog(ChangePasswordController.class);
	
	private ChangePasswordService changePasswordService;
	
	private String oldPassword;
	private String password;
	private String passwordRepeat;	
	
	public ChangePasswordService getChangePasswordService() {
		return changePasswordService;
	}
	public void setChangePasswordService(ChangePasswordService changePasswordService) {
		this.changePasswordService = changePasswordService;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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
	public String submit() {
		if (log.isDebugEnabled()) {
			log.debug("Clicked submit");
		}
		getChangePasswordService().updateChangeCurrentUserPassword(getPassword());
		
		JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, PersonUtils.getCurrentPersonId().toString());
		
		// As a redirect is used, this is actually useless.
		return null;
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

	public void validateOldPassword(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String oldPasswordFromUser = (String) value;
		String oldPasswordReal = getChangePasswordService().getCurrentUserPassword();
		if (oldPasswordFromUser == null || !oldPasswordFromUser.equals(oldPasswordReal)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_oldPasswordNotCorrect", locale);
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

	public String getPersonOverviewHref() {		
		return PersonUtils.getPersonOverviewPageUrl(PersonUtils.getCurrentPersonId().toString());
	}
}
