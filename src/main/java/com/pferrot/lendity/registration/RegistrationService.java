package com.pferrot.lendity.registration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.model.Country;
import com.pferrot.lendity.model.Gender;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.model.OrderedListValue;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PersonDetailsVisibility;
import com.pferrot.lendity.model.WallCommentsAddPermission;
import com.pferrot.lendity.model.WallCommentsVisibility;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.dao.RoleDao;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.Role;
import com.pferrot.security.model.User;
import com.pferrot.security.passwordgenerator.PasswordGenerator;

public class RegistrationService {
	
	private final static Log log = LogFactory.getLog(RegistrationService.class);
	
	private PersonDao personDao;
	private UserDao userDao;
	private RoleDao roleDao;
	private ListValueDao listValueDao;
	private MailManager mailManager;
	private PotentialConnectionService potentialConnectionService;

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

	public PotentialConnectionService getPotentialConnectionService() {
		return potentialConnectionService;
	}

	public void setPotentialConnectionService(
			PotentialConnectionService potentialConnectionService) {
		this.potentialConnectionService = potentialConnectionService;
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
	 * @throws RegistrationException 
	 */
	public Long createUser(final Person pPerson) throws RegistrationException {
		try {
			CoreUtils.assertNotNull(pPerson);
			CoreUtils.assertNotNull(pPerson.getUser());
			
			pPerson.getUser().setCreationDate(new Date());
			// Disabled until activation link is visited.
			pPerson.getUser().setEnabled(Boolean.FALSE);
			pPerson.setEnabled(Boolean.FALSE);
			Role userRole = roleDao.findRole(Role.USER_ROLE_NAME);
			
			// This convenience method also adds the user on the role.
			pPerson.getUser().addRole(userRole);
	
			// Activation code.
			final String activationCode = PasswordGenerator.getNewPassword(12);
			pPerson.getUser().setActivationCode(activationCode);
	
			// This will also create the user.
			Long personId = personDao.createPerson(pPerson);
			
			// Generate activation link.
			final StringBuffer activationLink = new StringBuffer(Configuration.getRootURL());
			activationLink.append(PagesURL.REGISTRATION_VALIDATION);
			activationLink.append("?");
			activationLink.append(RegistrationConsts.USERNAME_PARAMETER_NAME);
			activationLink.append("=");
			activationLink.append(URLEncoder.encode(pPerson.getUser().getUsername(), JsfUtils.URL_ENCODING));
			activationLink.append("&");
			activationLink.append(RegistrationConsts.ACTIVATION_CODE_PARAMETER_NAME);
			activationLink.append("=");
			activationLink.append(URLEncoder.encode(activationCode, JsfUtils.URL_ENCODING));
			
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("firstName", pPerson.getFirstName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			objects.put("activationUrl", activationLink.toString());
			objects.put("signature", Configuration.getSiteName());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/registration/validation/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(pPerson.getEmail(), pPerson.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
			
			mailManager.send(Configuration.getNoReplySenderName(), 
					 		 Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": validation de l'enregistrement",
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
			
			return personId;
		} catch (UnsupportedEncodingException e) {
			throw new RegistrationException(e);
		}
	}

	public void updateUserValidation(final String pUsername, final String pActivationCode) throws RegistrationException {
		try {
			if (StringUtils.isNullOrEmpty(pUsername) || StringUtils.isNullOrEmpty(pActivationCode)) {
				throw new RegistrationException("Username and activation code must not be null or empty.");
			}
			
			final User user = userDao.findUser(pUsername);
			if (user == null) {
				throw new RegistrationException("No user with username '" + pUsername + "'.");
			}
			else if (user.getActivationDate() != null) {
				throw new RegistrationException("User already activated: '" + pUsername + "'.");
			}
			else if (user.getEnabled() != null && user.getEnabled().booleanValue()) {
				throw new RegistrationException("User already enabled: '" + pUsername + "'.");
			}
			else if (! pActivationCode.equals(user.getActivationCode())) {
				throw new RegistrationException("Activation code '" + pActivationCode + 
						"' is not correct for: '" + pUsername + "'.");
			}
			
			final Person person = personDao.findPersonFromUsername(pUsername);
			if (person == null) {
				throw new RegistrationException("No person associated with username '" + pUsername + "'.");
			}
			else if (person.isEnabled()) {
				throw new RegistrationException("Person already enabled: '" + pUsername + "'.");
			}
			
			// All check done - we can activate the user.
			final Date now = new Date();
			user.setActivationDate(now);
			user.setEnabled(Boolean.TRUE);
			person.setJoinDate(now);
			person.setEnabled(Boolean.TRUE);
			// Do not send update the first night after the validation...
			person.setEmailSubscriberLastUpdate(now);
			getPotentialConnectionService().updateAndNotifyPotentialConnections(person);
			getPotentialConnectionService().createReverseInvitationPotentialConnections(person);
		}
		catch (PotentialConnectionException e) {
			throw new RegistrationException(e);
		}
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
	
	public PersonDetailsVisibility getDefaultPersonDetailsVisibility() {
		return (PersonDetailsVisibility)listValueDao.findListValue(PersonDetailsVisibility.PRIVATE);
	}
	
	public WallCommentsVisibility getDefaultWallCommentsVisibility() {
		return (WallCommentsVisibility)listValueDao.findListValue(WallCommentsVisibility.PUBLIC);
	}
	
	public WallCommentsAddPermission getDefaultWallCommentsAddPermission() {
		return (WallCommentsAddPermission)listValueDao.findListValue(WallCommentsAddPermission.CONNECTIONS);
	}

}
