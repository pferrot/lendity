package com.pferrot.lendity.registration.jsf;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.registration.RegistrationException;
import com.pferrot.lendity.registration.RegistrationService;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.security.model.User;

public class RegistrationController {
	
	private final static Log log = LogFactory.getLog(RegistrationController.class);
	
	private final static String CAPTCHA_SESSION_KEY_NAME = "registrationCaptchaSessionKeyName";
	
	private String firstName;
	private String lastName;
	private Date birthdate;
	private String displayName;
	private String email;
	private String phoneHome;
	private String phoneMobile;
	private String phoneProfessional;
	private String addressHome;
	private Double addressHomeLatitude;
	private Double addressHomeLongitude;
	private String password;
	private String passwordRepeat;
	private String captcha;
	private Boolean termsAndConditionsAccepted;
	
	private RegistrationService registrationService;
	
	private MessageDigestPasswordEncoder passwordEncoder;

	
	public void setPasswordEncoder(MessageDigestPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = StringUtils.getNullIfEmpty(firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = StringUtils.getNullIfEmpty(lastName);
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public void setBirthdateAsString(final String pBirthdateAsString) {
		try {
			if (StringUtils.isNullOrEmpty(pBirthdateAsString)) {
				setBirthdate(null);
			}
			else {
				setBirthdate(I18nUtils.getSimpleDateFormat().parse(pBirthdateAsString));
			}
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getBirthdateAsString() {
		if (getBirthdate() == null) {
			return "";
		}
		else {
			return I18nUtils.getSimpleDateFormat().format(getBirthdate());
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = StringUtils.getNullIfEmpty(displayName);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = StringUtils.getNullIfEmpty(email);
	}	
	
	public String getPhoneHome() {
		return phoneHome;
	}

	public void setPhoneHome(String phoneHome) {
		this.phoneHome = StringUtils.getNullIfEmpty(phoneHome);
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = StringUtils.getNullIfEmpty(phoneMobile);
	}

	public String getPhoneProfessional() {
		return phoneProfessional;
	}

	public void setPhoneProfessional(String phoneProfessional) {
		this.phoneProfessional = StringUtils.getNullIfEmpty(phoneProfessional);
	}

	public String getAddressHome() {
		return addressHome;
	}

	public void setAddressHome(String addressHome) {
		this.addressHome = StringUtils.getNullIfEmpty(addressHome);
	}	
	
	public Double getAddressHomeLatitude() {
		return addressHomeLatitude;
	}

	public void setAddressHomeLatitude(Double addressHomeLatitude) {
		this.addressHomeLatitude = addressHomeLatitude;
	}

	public Double getAddressHomeLongitude() {
		return addressHomeLongitude;
	}

	public void setAddressHomeLongitude(Double addressHomeLongitude) {
		this.addressHomeLongitude = addressHomeLongitude;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = StringUtils.getNullIfEmpty(password);
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = StringUtils.getNullIfEmpty(passwordRepeat);
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = StringUtils.getNullIfEmpty(captcha);
	}

	public Boolean getTermsAndConditionsAccepted() {
		return termsAndConditionsAccepted;
	}

	public void setTermsAndConditionsAccepted(Boolean termsAndConditionsAccepted) {
		this.termsAndConditionsAccepted = termsAndConditionsAccepted;
	}

	public String getCaptchaSessionKeyName() {
        return CAPTCHA_SESSION_KEY_NAME;
    }

	public String getNbDaysToValidateRegistration() {
		return String.valueOf(Configuration.getNbDaysToValidateRegistration());
	}

	public String getAddressHomeFormated() {
		final String address = getAddressHome();
		if (address != null) {
			return HtmlUtils.escapeHtmlAndReplaceCr(address);
		}
		return "";
	}

	public boolean isAddressHomeAvailable() {
		final String address = getAddressHome();
		return !StringUtils.isNullOrEmpty(address);
	}
	
	public void createUser() {
		try {
			User user = new User();
			user.setUsername(getEmail());
			// If encoding the password, do not forget to update applicationContext-security.xml in the security module.
			user.setPassword(passwordEncoder.encodePassword(getPassword(), null));
		
			Person person = new Person();
			person.setFirstName(getFirstName());
			person.setLastName(getLastName());
			person.setBirthdate(getBirthdate());
			person.setDisplayName(getDisplayName());
			person.setEmail(getEmail());
			person.setPhoneHome(getPhoneHome());
			person.setPhoneMobile(getPhoneMobile());
			person.setPhoneProfessional(getPhoneProfessional());
			person.setAddressHome(getAddressHome());
			// Needed because validateAddressHome() is not called if empty. 
			if (StringUtils.isNullOrEmpty(getAddressHome())) {
				setAddressHomeLatitude(null);
				setAddressHomeLongitude(null);
			}
			person.setAddressHomeLatitude(getAddressHomeLatitude());
			person.setAddressHomeLongitude(getAddressHomeLongitude());
			person.setUser(user);
			person.setEmailSubscriber(Boolean.TRUE);
			person.setReceiveNeedsNotifications(Boolean.TRUE);
			person.setReceiveCommentsOnCommentedNotif(Boolean.TRUE);
			person.setReceiveCommentsOnOwnNotif(Boolean.TRUE);
			person.setShowContactDetailsToAll(Boolean.FALSE);
			person.setNbEvalScore1(Integer.valueOf(0));
			person.setNbEvalScore2(Integer.valueOf(0));
			
			registrationService.createUser(person);
		} catch (RegistrationException e) {
			throw new RuntimeException(e);
		}
	}
}
