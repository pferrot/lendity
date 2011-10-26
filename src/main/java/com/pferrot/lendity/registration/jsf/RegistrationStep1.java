package com.pferrot.lendity.registration.jsf;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.geolocation.bean.Coordinate;
import com.pferrot.lendity.geolocation.exception.GeolocalisationException;
import com.pferrot.lendity.geolocation.googlemaps.GoogleMapsUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.iptocountry.IpToCountryService;
import com.pferrot.lendity.person.PersonConsts;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.registration.RegistrationConsts;
import com.pferrot.lendity.registration.RegistrationService;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/registration/registration.jspx"})
public class RegistrationStep1 {
	
	private final static Log log = LogFactory.getLog(RegistrationStep1.class);
	
	private RegistrationController registrationController;
	private RegistrationService registrationService;
	private PersonService personService;
	private IpToCountryService ipToCountryService;
	
	public RegistrationStep1() {
		super();
	}
	
	@InitView
	public void initView() {
		final String ipAddress = ((HttpServletRequest)JsfUtils.getRequest()).getRemoteAddr();
		if (!getIpToCountryService().isIpInSwitzerland(ipAddress)) {
			JsfUtils.redirect(PagesURL.REGISTRATION_NOT_IN_YOUR_COUNTRY);
			return;
		}
		final String email = JsfUtils.getRequestParameter(RegistrationConsts.REGISTRATION_EMAIL_PARAM_NAME);
		if (!StringUtils.isNullOrEmpty(email)) {
			getRegistrationController().setEmail(email.trim());
		}
	}
	
	public IpToCountryService getIpToCountryService() {
		return ipToCountryService;
	}

	public void setIpToCountryService(IpToCountryService ipToCountryService) {
		this.ipToCountryService = ipToCountryService;
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

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
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
			final Locale locale = I18nUtils.getDefaultLocale();
			message = I18nUtils.getMessageResourceString("validation_emailNotValid", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
		else if (!registrationService.isUsernameAvailable(email)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = I18nUtils.getDefaultLocale();
			message = I18nUtils.getMessageResourceString("validation_userAlreadyExists", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}

	public void validateDisplayName(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String displayName = (String) value;
		if (!personService.isDisplayNameAvailable(displayName)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = I18nUtils.getDefaultLocale();
			message = I18nUtils.getMessageResourceString("person_displayNameAlreadyExists", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
	
	public void validateBirthdate(FacesContext context, UIComponent toValidate, Object value) {
		try {
			String message = "";
			final String birthdateString = (String) value;
			final Date birthdate = I18nUtils.getSimpleDateFormat().parse(birthdateString);
			final Date d = new Date();
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, c.get(Calendar.YEAR) - Configuration.getMinimumAge());
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			
			if (c.getTime().before(birthdate)) {
				((UIInput)toValidate).setValid(false);
				final Locale locale = I18nUtils.getDefaultLocale();
				message = I18nUtils.getMessageResourceString("validation_birthdateTooYoung", locale);
				context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public void validatePassword(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String password = (String) value;
		if (password == null || !password.matches(RegistrationConsts.PASSWORD_REGEXP)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = I18nUtils.getDefaultLocale();
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
			final Locale locale = I18nUtils.getDefaultLocale();
			message = I18nUtils.getMessageResourceString("validation_captchaWrong", locale);
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
			final Locale locale = I18nUtils.getDefaultLocale();
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
			final Locale locale = I18nUtils.getDefaultLocale();
			message = I18nUtils.getMessageResourceString("validation_termsAndConditionsNotAccepted", locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}

	public void validateAddressHome(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String address = (String) value;
		if (!StringUtils.isNullOrEmpty(address)) {
			if (address.length() > PersonConsts.MAX_ADDRESS_SIZE) {
				((UIInput)toValidate).setValid(false);
				final Locale locale = I18nUtils.getDefaultLocale();
				message = I18nUtils.getMessageResourceString("validation_maxSizeExceeded", new Object[]{String.valueOf(PersonConsts.MAX_ADDRESS_SIZE)}, locale);
				context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
			}
			
			else {
				try {
					final Coordinate c = GoogleMapsUtils.getCoordinate(address);
					getRegistrationController().setAddressHomeLatitude(c.getLatitude());
					getRegistrationController().setAddressHomeLongitude(c.getLongitude());
				}
				catch (GeolocalisationException e) {
					getRegistrationController().setAddressHomeLatitude(null);
					getRegistrationController().setAddressHomeLongitude(null);
					
					((UIInput)toValidate).setValid(false);
					final Locale locale = I18nUtils.getDefaultLocale();
					message = I18nUtils.getMessageResourceString("validation_geolocationNotFound", locale);
					context.addMessage(toValidate.getClientId(context), new FacesMessage(message));					
				}
			}
		}
		else {
			getRegistrationController().setAddressHomeLatitude(null);
			getRegistrationController().setAddressHomeLongitude(null);
		}
	}

}
