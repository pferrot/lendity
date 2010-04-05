package com.pferrot.sharedcalendar.lostpassword;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.Consts;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.configuration.Configuration;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Person;

public class LostPasswordService {
	
	private final static Log log = LogFactory.getLog(LostPasswordService.class);
	
	private PersonDao personDao;
	private UserDao userDao;
	private MailManager mailManager;

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * Returns true the specified username exists and is enabled.
	 * 
	 * @param username
	 * @return
	 */
	public boolean isUsernameExistingAndEnabled(final String pEmail) {
		CoreUtils.assertNotNullOrEmptyString(pEmail);
		
		// The email is the username.
		final String username = pEmail;
		
		User user = userDao.findUser(username);
		
		return user != null && user.getEnabled();
	}
	
	/**
	 * This operation will create a JMS message to send an email with the password.
	 * 
	 * @param person
	 * @return
	 */
	public boolean sendPassword(final String pEmail) {
		CoreUtils.assertNotNull(pEmail);
		
		// The email is the username.
		final String username = pEmail;
		
		final Person person = personDao.findPersonFromUsername(username);
		if (person == null || person.getUser() == null || !person.getUser().getEnabled()) {
			return false;
		}
		
		CoreUtils.assertNotNullOrEmptyString(person.getEmail());
		CoreUtils.assertNotNullOrEmptyString(person.getUser().getPassword());
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("firstName", person.getFirstName());
		objects.put("username", person.getUser().getUsername());
		objects.put("password", person.getUser().getPassword());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/sharedcalendar/emailtemplate/lostpassword/en";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(person.getEmail(), person.getEmail());
		
		mailManager.send(Configuration.getNoReplySenderName(), 
						 Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         "Your password for " + Configuration.getSiteName(),
				         objects, 
				         velocityTemplateLocation);		
		
		return true;		
	}
}
