package com.pferrot.sharedcalendar.registration.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.ConversationUtils;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.registration.RegistrationService;

@ViewController(viewIds={"/public/registration/registration.jspx", "/public/registration/registration_2.jspx", "/public/registration/registration_3.jspx"})
public class RegistrationViewController
	//implements org.apache.myfaces.orchestra.viewController.ViewController
{
	private final static Log log = LogFactory.getLog(RegistrationViewController.class);
	
	private String username;
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
	
	public void createUser() {
		User user = new User();
		user.setUsername(getUsername());
	
		Person person = new Person();
		person.setFirstName(getFirstName());
		person.setLastName(getLastName());
		person.setEmail(getEmail());
		person.setUser(user);
		
		registrationService.createUser(person);		
	}
}
