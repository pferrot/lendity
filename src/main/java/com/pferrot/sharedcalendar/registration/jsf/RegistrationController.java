package com.pferrot.sharedcalendar.registration.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Address;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.registration.RegistrationService;
import com.pferrot.sharedcalendar.utils.UiUtils;

public class RegistrationController {
	
	private final static Log log = LogFactory.getLog(RegistrationController.class);
	
	private Gender gender;
	private Long genderId;
	private List<SelectItem> gendersSelectItems;
	private String firstName;
	private String lastName;
	private String email;	
	private RegistrationService registrationService;

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	public void createUser() {
		User user = new User();
		user.setUsername(getEmail());
	
		Person person = new Person();
		person.setGender(registrationService.findGender(getGenderId()));
		person.setFirstName(getFirstName());
		person.setLastName(getLastName());
		person.setEmail(getEmail());
		person.setUser(user);
		
		// TODO: user is not asked to enter his address during registration.
		person.setAddress(new Address());		
		
		registrationService.createUser(person);		
	}
}
