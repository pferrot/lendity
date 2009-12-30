package com.pferrot.sharedcalendar.registration;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.Consts;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.security.dao.RoleDao;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.Role;
import com.pferrot.security.model.User;
import com.pferrot.security.passwordgenerator.PasswordGenerator;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Country;
import com.pferrot.sharedcalendar.model.Gender;
import com.pferrot.sharedcalendar.model.ListValue;
import com.pferrot.sharedcalendar.model.OrderedListValue;
import com.pferrot.sharedcalendar.model.Person;

public class RegistrationService {
	
	private final static Log log = LogFactory.getLog(RegistrationService.class);
	
	private PersonDao personDao;
	private UserDao userDao;
	private RoleDao roleDao;
	private ListValueDao listValueDao;
	private MessageDigestPasswordEncoder passwordEncoder;
	private MailManager mailManager;

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
	public boolean isUsernameAvailable(final String pUsername) {
		CoreUtils.assertNotNullOrEmptyString(pUsername);
		
		User user = userDao.findUser(pUsername);
		
		return user == null;
	}
	
	/**
	 * This operation will:
	 * - generate a random password
	 * - create the User in the DB
	 * - create the Person in the DB
	 * - create a JMS message to send an email with the password
	 * 
	 * @param person
	 * @return
	 */
	public Long createUser(final Person pPerson) {
		CoreUtils.assertNotNull(pPerson);
		CoreUtils.assertNotNull(pPerson.getUser());
		
		pPerson.getUser().setCreationDate(new Date());
		pPerson.getUser().setEnabled(Boolean.TRUE);
		
		Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
		
		// This convenience method also adds the user on the role.
		pPerson.getUser().addRole(userRole);		
		
//		final String rawPassword = PasswordGenerator.getNewPassword();
		// If encoding the password, do not forget to update applicationContext-security.xml in the security module.
//		final String md5EncodedPassword = passwordEncoder.encodePassword(rawPassword, null);
//		if (log.isDebugEnabled()) {
//			log.debug("Generated password for user '" + pPerson.getUser().getUsername() + 
//					"': '" + rawPassword + "' ('" + md5EncodedPassword + "')");
//		}
//		pPerson.getUser().setPassword(md5EncodedPassword);
//		pPerson.getUser().setPassword(rawPassword);

		// This will also create the user.
		Long personId = personDao.createPerson(pPerson);
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("firstName", pPerson.getFirstName());
		objects.put("username", pPerson.getUser().getUsername());
		objects.put("password", pPerson.getUser().getPassword());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/sharedcalendar/emailtemplate/registration/logindetails/en";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(pPerson.getEmail(), pPerson.getEmail());
		
		mailManager.send(Consts.DEFAULT_SENDER_NAME, 
				         Consts.DEFAULT_SENDER_ADDRESS,
				         to,
				         null, 
				         null,
				         "Your registration on sharedcalendar.com",
				         objects, 
				         velocityTemplateLocation);		
		
		return personId;		
	}
	
	public List<OrderedListValue> getGenders() {
		return listValueDao.findOrderedListValue(Gender.class);
	}

	public Gender findGender(Long id) {
		return (Gender)listValueDao.findListValue(id);
	}

	public List<ListValue> getCountries() {
		return listValueDao.findListValue(Country.class);
	}

	public Country findCountry(Long id) {
		return (Country)listValueDao.findListValue(id);
	}

	public Country findCountry(String labelCode) {
		return (Country)listValueDao.findListValue(labelCode);
	}
	
	
//	public boolean isUsernameValid() {
//		
//	}
	
	
	

}
