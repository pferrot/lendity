package com.pferrot.lendity.registration.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.security.model.User;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.model.Address;
import com.pferrot.lendity.model.Country;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.registration.RegistrationException;
import com.pferrot.lendity.registration.RegistrationService;
import com.pferrot.lendity.utils.UiUtils;

public class RegistrationController {
	
	private final static Log log = LogFactory.getLog(RegistrationController.class);
	
	private final static String CAPTCHA_SESSION_KEY_NAME = "registrationCaptchaSessionKeyName";
	
	private Long genderId;
	private List<SelectItem> gendersSelectItems;
	private String firstName;
	private String lastName;
	private String displayName;
	private String email;
	private String phoneHome;
	private String phoneMobile;
	private String phoneProfessional;
	private String address1;
	private String address2;
	private Integer zip;
	private String city;
	private Long countryId;
	private List<SelectItem> countriesSelectItems;
	private String password;
	private String passwordRepeat;
	private String captcha;
	private String betaCode;
	private Boolean termsAndConditionsAccepted;
	
	private RegistrationService registrationService;


	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
		// Default country to switzerland.
		setCountryId(registrationService.findCountry(Country.SWITZERLAND_LABEL_CODE).getId());
	}	

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Integer getZip() {
		return zip;
	}

	public void setZip(Integer zip) {
		this.zip = zip;
	}	

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getGenderId() {
		return genderId;
	}

	public void setGenderId(Long genderId) {
		this.genderId = genderId;
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}	
	
	public String getPhoneHome() {
		return phoneHome;
	}

	public void setPhoneHome(String phoneHome) {
		this.phoneHome = phoneHome;
	}

	public String getPhoneMobile() {
		return phoneMobile;
	}

	public void setPhoneMobile(String phoneMobile) {
		this.phoneMobile = phoneMobile;
	}

	public String getPhoneProfessional() {
		return phoneProfessional;
	}

	public void setPhoneProfessional(String phoneProfessional) {
		this.phoneProfessional = phoneProfessional;
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

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getBetaCode() {
		return betaCode;
	}

	public void setBetaCode(String betaCode) {
		this.betaCode = betaCode;
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

	public List<SelectItem> getGendersSelectItems() {
		if (gendersSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			gendersSelectItems = UiUtils.getSelectItemsForOrderedListValue(registrationService.getGenders(), locale);
			// Add "Please select..." first.
			gendersSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return gendersSelectItems;	
	}
	
	public List<SelectItem> getCountriesSelectItems() {
		if (countriesSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			countriesSelectItems = UiUtils.getSelectItemsForListValue(registrationService.getCountries(), locale);
			// Add "Please select..." first.
			countriesSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return countriesSelectItems;	
	}	
	
	public void createUser() {
		try {
			User user = new User();
			user.setUsername(getEmail());
			user.setPassword(getPassword());
		
			Person person = new Person();
	//		person.setGender(registrationService.findGender(getGenderId()));
			person.setFirstName(getFirstName());
			person.setLastName(getLastName());
	//		person.setDisplayName(getDisplayName());
			person.setEmail(getEmail());
	//		person.setPhoneHome(getPhoneHome());
	//		person.setPhoneMobile(getPhoneMobile());
	//		person.setPhoneProfessional(getPhoneProfessional());
			person.setUser(user);
			person.setEmailSubscriber(Boolean.TRUE);
			person.setReceiveNeedsNotifications(Boolean.TRUE);
			person.setNbInvitations(Configuration.getNbInvitationsNewUsers());
			
			final Address address = new Address();
	//		address.setAddress1(getAddress1());
	//		address.setAddress2(getAddress2());
	//		address.setZip(getZip());
	//		address.setCity(getCity());
	//		address.setCountry(registrationService.findCountry(getCountryId()));
			person.setAddress(address);		
			
			registrationService.createUser(person);
		} catch (RegistrationException e) {
			throw new RuntimeException(e);
		}
	}
}
