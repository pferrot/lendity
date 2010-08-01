package com.pferrot.lendity.lostpassword;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Person;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.User;

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
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lostpassword/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(person.getEmail(), person.getEmail());
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
		
		mailManager.send(Configuration.getNoReplySenderName(), 
						 Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         Configuration.getSiteName() + ": rappel du mot de passe", 
				         objects, 
				         velocityTemplateLocation,
				         inlineResources);		
		
		return true;		
	}
}
