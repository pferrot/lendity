package com.pferrot.lendity.lostpassword;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.MessageDigestPasswordEncoder;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.dao.PersonDao;
import com.pferrot.lendity.lostpassword.exception.LostPasswordException;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.security.dao.UserDao;
import com.pferrot.security.model.User;
import com.pferrot.security.passwordgenerator.PasswordGenerator;

public class LostPasswordService {
	
	private final static Log log = LogFactory.getLog(LostPasswordService.class);
	
	private PersonDao personDao;
	private UserDao userDao;
	private MailManager mailManager;
	private MessageDigestPasswordEncoder passwordEncoder;

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setPasswordEncoder(MessageDigestPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
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
	 * This operation will generate a new "reset password code" for the user
	 * and send the link to reset the password.
	 * 
	 * @param person
	 * @return
	 * @throws LostPasswordException 
	 */
	public boolean updateSendResetPasswordInstructions(final String pEmail) throws LostPasswordException {
		try {
			CoreUtils.assertNotNull(pEmail);
			
			// The email is the username.
			final String username = pEmail;
			
			final Person person = personDao.findPersonFromUsername(username);
			if (person == null || person.getUser() == null || !person.getUser().getEnabled()) {
				return false;
			}
			
			
			
			final String resetPasswordCode = PasswordGenerator.getNewPassword(20);
			final User user = userDao.findUser(pEmail);
			user.setResetPasswordCode(resetPasswordCode);
			
			// Generate activation link.
			final StringBuffer resetPasswordUrl = new StringBuffer(Configuration.getRootURL());
			resetPasswordUrl.append(PagesURL.RESET_PASSWORD);
			resetPasswordUrl.append("?");
			resetPasswordUrl.append(LostPasswordConsts.USERNAME_PARAMETER_NAME);
			resetPasswordUrl.append("=");
			resetPasswordUrl.append(URLEncoder.encode(pEmail, JsfUtils.URL_ENCODING));
			resetPasswordUrl.append("&");
			resetPasswordUrl.append(LostPasswordConsts.RESET_PASSWORD_CODE_PARAMETER_NAME);
			resetPasswordUrl.append("=");
			resetPasswordUrl.append(URLEncoder.encode(resetPasswordCode, JsfUtils.URL_ENCODING));
			
			// Send email (will actually create a JMS message, i.e. it is async).
			Map<String, String> objects = new HashMap<String, String>();
			objects.put("firstName", person.getFirstName());
			objects.put("username", person.getUser().getUsername());
			objects.put("resetPasswordUrl", resetPasswordUrl.toString());
			objects.put("signature", Configuration.getSiteName());
			objects.put("siteName", Configuration.getSiteName());
			objects.put("siteUrl", Configuration.getRootURL());
			
			// TODO: localization
			final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lostpassword/instructions/fr";
			
			Map<String, String> to = new HashMap<String, String>();
			to.put(person.getEmail(), person.getEmail());
			
			Map<String, String> inlineResources = new HashMap<String, String>();
			inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
			
			mailManager.send(Configuration.getNoReplySenderName(), 
							 Configuration.getNoReplyEmailAddress(),
					         to,
					         null, 
					         null,
					         Configuration.getSiteName() + ": mot de passe oublié?", 
					         objects, 
					         velocityTemplateLocation,
					         inlineResources);		
			
			return true;	
		}
		catch (UnsupportedEncodingException e) {
			throw new LostPasswordException(e);
		}
	}

	public void updateResetPassword(final String pUsername, final String pResetPasswordCode) throws LostPasswordException {
		if (StringUtils.isNullOrEmpty(pUsername) || StringUtils.isNullOrEmpty(pResetPasswordCode)) {
			throw new LostPasswordException("Username and reset password code must not be null or empty.");
		}
		
		final User user = userDao.findUser(pUsername);
		if (user == null) {
			throw new LostPasswordException("No user with username '" + pUsername + "'.");
		}
		else if (user.getEnabled() == null || !user.getEnabled().booleanValue()) {
			throw new LostPasswordException("User disabled: '" + pUsername + "'.");
		}
		else if (!pResetPasswordCode.equals(user.getResetPasswordCode())) {
			throw new LostPasswordException("Wrong reset password code: '" + pResetPasswordCode + "' for user '" + pUsername + "'.");
		}
		
		final String rawPassword = PasswordGenerator.getNewPassword();
		final String md5EncodedPassword = passwordEncoder.encodePassword(rawPassword, null);
		if (log.isDebugEnabled()) {
			log.debug("Generated password for user '" + pUsername + 
					"': '" + rawPassword + "' ('" + md5EncodedPassword + "')");
		}
		
		user.setPassword(md5EncodedPassword);
		// Avoid that the code is reset twice in case the user visits twice the page.
		user.setResetPasswordCode(null);
		userDao.updateUser(user);
		
		final Person person = personDao.findPersonFromUsername(pUsername);
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("firstName", person.getFirstName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		objects.put("username", user.getUsername());
		objects.put("password", rawPassword);
		objects.put("signature", Configuration.getSiteName());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/lostpassword/reset/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(person.getEmail(), person.getEmail());
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
		
		mailManager.send(Configuration.getNoReplySenderName(), 
						 Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         Configuration.getSiteName() + ": nouveau mot de passe",
				         objects, 
				         velocityTemplateLocation,
				         inlineResources);		

	}
}
