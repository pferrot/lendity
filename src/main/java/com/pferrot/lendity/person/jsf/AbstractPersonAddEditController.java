package com.pferrot.lendity.person.jsf;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractPersonAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractPersonAddEditController.class);
	
	private PersonService personService;
	
	private String firstName;
	private String lastName;
	private Date birthdate;
	private String displayName;
	
	private String website;
	
	private String phoneHome;
	private String phoneMobile;
	private String phoneProfessional;
	
	private String addressHome;
	private Double addressHomeLongitude;
	private Double addressHomeLatitude;
	
	private Boolean emailSubscriber;
	private Boolean receiveNeedsNotifications;
	private Boolean receiveCommentsOnOwnNotif;
	private Boolean receiveCommentsOnCommentedNotif;
	private Boolean showContactDetailsToAll;
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
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

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = StringUtils.getNullIfEmpty(website);
	}

	public String getAddressHome() {
		return addressHome;
	}

	public void setAddressHome(String addressHome) {
		this.addressHome = StringUtils.getNullIfEmpty(addressHome);
	}

	public Double getAddressHomeLongitude() {
		return addressHomeLongitude;
	}

	public void setAddressHomeLongitude(Double addressHomeLongitude) {
		this.addressHomeLongitude = addressHomeLongitude;
	}

	public Double getAddressHomeLatitude() {
		return addressHomeLatitude;
	}

	public void setAddressHomeLatitude(Double addressHomeLatitude) {
		this.addressHomeLatitude = addressHomeLatitude;
	}

	public Boolean getEmailSubscriber() {
		return emailSubscriber;
	}

	public void setEmailSubscriber(Boolean emailSubscriber) {
		this.emailSubscriber = emailSubscriber;
	}

	public Boolean getReceiveNeedsNotifications() {
		return receiveNeedsNotifications;
	}

	public void setReceiveNeedsNotifications(Boolean receiveNeedsNotifications) {
		this.receiveNeedsNotifications = receiveNeedsNotifications;
	}

	public Boolean getReceiveCommentsOnOwnNotif() {
		return receiveCommentsOnOwnNotif;
	}

	public void setReceiveCommentsOnOwnNotif(Boolean receiveCommentsOnOwnNotif) {
		this.receiveCommentsOnOwnNotif = receiveCommentsOnOwnNotif;
	}

	public Boolean getReceiveCommentsOnCommentedNotif() {
		return receiveCommentsOnCommentedNotif;
	}

	public void setReceiveCommentsOnCommentedNotif(
			Boolean receiveCommentsOnCommentedNotif) {
		this.receiveCommentsOnCommentedNotif = receiveCommentsOnCommentedNotif;
	}

	public Boolean getShowContactDetailsToAll() {
		return showContactDetailsToAll;
	}

	public void setShowContactDetailsToAll(Boolean showContactDetailsToAll) {
		this.showContactDetailsToAll = showContactDetailsToAll;
	}

	public abstract Long processPerson();
	
	public String submit() {
		processPerson();
		
		JsfUtils.redirect(PagesURL.MY_PROFILE);
	
		// As a redirect is used, this is actually useless.
		return null;
	}	
}
