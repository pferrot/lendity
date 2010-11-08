package com.pferrot.lendity.invitation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.registration.RegistrationService;

public class InvitationService {
	
	private final static Log log = LogFactory.getLog(InvitationService.class);
	
	private RegistrationService registrationService;
	private PersonService personService;
	private MailManager mailManager;
	
	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	/**
	 * Sends an invitation.
	 *
	 * @param pPerson The person sending the invitation.
	 * @param pEmail The email of the person to invite.
	 * @return
	 * @throws InvitationException
	 */
	public void sendInvitation(final Long pPersonId, final String pEmail) throws InvitationException {
		CoreUtils.assertNotNull(pPersonId);
		CoreUtils.assertNotNullOrEmptyString(pEmail);
		
		final Person pPerson = personService.findPerson(pPersonId);
		
		if (!getRegistrationService().isUsernameAvailable(pEmail)) {
			throw new AlreadyUserInvitationException("Already user with username " + pEmail);
		}
		
		// Generate link.
		final StringBuffer registrationLink = new StringBuffer(Configuration.getRootURL());
		registrationLink.append(PagesURL.REGISTRATION);
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("firstName", pPerson.getFirstName());
		objects.put("lastName", pPerson.getLastName());
		objects.put("email", pEmail);
		objects.put("registrationUrl", registrationLink.toString());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/invitation/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(pEmail, pEmail);
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.gif");
		
		mailManager.send(pPerson.getDisplayName(), 
				 		 pPerson.getEmail(),
				         to,
				         null, 
				         null,
				         Configuration.getSiteName() + ": invitation � ouvrir un compte",
				         objects, 
				         velocityTemplateLocation,
				         inlineResources);		
	}

}
