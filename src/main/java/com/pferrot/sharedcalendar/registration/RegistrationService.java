package com.pferrot.sharedcalendar.registration;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.emailsender.Consts;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.security.dao.RoleDao;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.Role;
import com.pferrot.security.model.User;
import com.pferrot.security.passwordgenerator.PasswordGenerator;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.OrderedListValue;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.emailsender.jms.EmailToSendProducer;
public class RegistrationService {
	
	private final static Log log = LogFactory.getLog(RegistrationService.class);
	
	private PersonDao personDao;
	private UserDao userDao;
	private RoleDao roleDao;
	private ListValueDao listValueDao;
	private MessageDigestPasswordEncoder passwordEncoder;
	private MailManager mailManager;	
	private EmailToSendProducer emailToSendProducer;
	
	public void setEmailToSendProducer(EmailToSendProducer emailToSendProducer) {
		this.emailToSendProducer = emailToSendProducer;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
	
	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}	
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public void setPasswordEncoder(MessageDigestPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Return true the specified username is not already used.
	 * 
	 * @param username
	 * @return
	 */
	public boolean isUsernameAvailable(final String username) {
		if (username == null) {
			throw new IllegalArgumentException("Parameter 'username' must not be null");
		}
		
		User user = userDao.findUser(username);
		
		return user == null;
	}
	
	/**
	 * This operation will:
	 * - generate a random password
	 * - create the User in the DB
	 * - create the Person in the DB
	 * - send an email with the password
	 * 
	 * @param person
	 * @return
	 */
	public Long createUser(final Person person) {
		if (person == null) {
			throw new IllegalArgumentException("Parameter 'person' must not be null");
		}
		else if (person.getUser() == null) {
			throw new IllegalArgumentException("The 'person' parameter must contain a new user object");
		}
		
		person.getUser().setCreationDate(new Date());
		person.getUser().setEnabled(Boolean.TRUE);
		
		Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
		
		// This convenience method also adds the user on the role.
		person.getUser().addRole(userRole);		
		
		final String rawPassword = PasswordGenerator.getNewPassword();		
		final String md5EncodedPassword = passwordEncoder.encodePassword(rawPassword, null);
		if (log.isDebugEnabled()) {
			log.debug("Generated password for user '" + person.getUser().getUsername() + 
					"': '" + rawPassword + "' ('" + md5EncodedPassword + "')");
		}
		person.getUser().setPassword(md5EncodedPassword);
		
		// Send email.
		Map objects = new HashMap();
		objects.put("firstName", person.getFirstName());
		objects.put("username", person.getUser().getUsername());
		objects.put("password", rawPassword);
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/sharedcalendar/registration/emailtemplate/en";
		final String bodyText = mailManager.getText(objects, velocityTemplateLocation);
		final String bodyHtml = mailManager.getHtml(objects, velocityTemplateLocation);
		
		emailToSendProducer.sendMessage(Consts.DEFAULT_SENDER_NAME, Consts.DEFAULT_SENDER_ADDRESS, 
				person.getEmail(), "Your registration on sharedcalendar.com", bodyText, bodyHtml);
		
		// This will also create the user.
		Long personId = personDao.createPerson(person);
		
		return personId;		
	}
	
	public List<OrderedListValue> getGenders() {
		return listValueDao.findOrderedListValue(Gender.class);
		
	}

	public Gender findGender(Long id) {
		return (Gender)listValueDao.findListValue(id);
	}
	
	
//	public boolean isUsernameValid() {
//		
//	}
	
	
	

}
