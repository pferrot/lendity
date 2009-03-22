package com.pferrot.sharedcalendar.registration.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Address;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.registration.RegistrationService;
import com.pferrot.sharedcalendar.utils.UiUtils;

@ViewController(viewIds={"/public/registration/registration.jspx", "/public/registration/registration_2.jspx", "/public/registration/registration_3.jspx"})
public class RegistrationViewController
	//implements org.apache.myfaces.orchestra.viewController.ViewController
{
	private final static Log log = LogFactory.getLog(RegistrationViewController.class);
	
	private String username;
	private Gender gender;
	private Long genderId;
	private List<SelectItem> gendersSelectItems;
	private String firstName;
	private String lastName;
	private String email;	
	private RegistrationService registrationService;

	@InitView
	public void initView() {
		ConversationUtils.ensureConversationRedirect("registration", "/public/registration/registration.iface");
	}

//	@PreProcess
//	public void preProcess() {		
//	}
//
//	@PreRenderView
//	public void preRenderView() {		
//	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
		user.setUsername(getUsername());
	
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
