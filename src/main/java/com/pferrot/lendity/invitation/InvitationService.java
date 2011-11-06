package com.pferrot.lendity.invitation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.emailsender.manager.MailManager;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.configuration.Configuration;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.model.PotentialConnection;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.potentialconnection.exception.PotentialConnectionException;
import com.pferrot.lendity.registration.RegistrationService;
import com.pferrot.lendity.utils.JsfUtils;

public class InvitationService {
	
	private final static Log log = LogFactory.getLog(InvitationService.class);
	
	private RegistrationService registrationService;
	private PersonService personService;
	private MailManager mailManager;
	private PotentialConnectionService potentialConnectionService;
	
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

	public PotentialConnectionService getPotentialConnectionService() {
		return potentialConnectionService;
	}

	public void setPotentialConnectionService(
			PotentialConnectionService potentialConnectionService) {
		this.potentialConnectionService = potentialConnectionService;
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
		
		final Person person = personService.findPerson(pPersonId);
		
		if (!getRegistrationService().isUsernameAvailable(pEmail)) {
			throw new AlreadyUserInvitationException("Already user with username " + pEmail);
		}
		
		// Generate link.
		final StringBuffer registrationLink = new StringBuffer(Configuration.getRootURL());
		registrationLink.append(PagesURL.REGISTRATION);
		
		// Send email (will actually create a JMS message, i.e. it is async).
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("personFirstName", person.getFirstName());
		objects.put("personLastName", person.getLastName());
		objects.put("personDisplayName", person.getDisplayName());
		objects.put("personUrl", JsfUtils.getFullUrlWithPrefix(Configuration.getRootURL(), PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, person.getId().toString()));
		objects.put("personEmail", person.getEmail());
		objects.put("registrationUrl", registrationLink.toString());
		objects.put("signature", Configuration.getSiteName());
		objects.put("siteName", Configuration.getSiteName());
		objects.put("siteUrl", Configuration.getRootURL());
		
		// TODO: localization
		final String velocityTemplateLocation = "com/pferrot/lendity/emailtemplate/invitation/fr";
		
		Map<String, String> to = new HashMap<String, String>();
		to.put(pEmail, pEmail);
		
		Map<String, String> inlineResources = new HashMap<String, String>();
		//inlineResources.put("logo", "com/pferrot/lendity/emailtemplate/lendity_logo.png");
		inlineResources.put("logo_signature", "com/pferrot/lendity/emailtemplate/lendity_signature1.gif");
		
		mailManager.send(Configuration.getNoReplySenderName(), 
		         		 Configuration.getNoReplyEmailAddress(),
				         to,
				         null, 
				         null,
				         person.getFirstName() + " " + person.getLastName() + " t'invite à le rejoindre sur LENDITY.CH",
				         objects, 
				         velocityTemplateLocation,
				         inlineResources);		
	}
	
	/**
	 * Send the invitation + create/update a potential connection.
	 * 
	 * @param pPersonId
	 * @param pEmail
	 * @throws InvitationException
	 */
	public void updateSendInvitationAndAddPotentialConnection(final Long pPersonId, final String pEmail) throws InvitationException {
		try {
			sendInvitation(pPersonId, pEmail);
			final Date now = new Date();
			final PotentialConnection pc = new PotentialConnection();
			pc.setPerson(personService.findPerson(pPersonId));
			pc.setEmail(pEmail);
			pc.setInvitationSentOn(now);
			pc.setDateAdded(now);
			pc.setIgnored(Boolean.FALSE);
			pc.setSource(PotentialConnection.SOURCE_INVITATION);
			getPotentialConnectionService().createOrUpdatePotentialConnection(pc);
		}
		catch (PotentialConnectionException e) {
			throw new InvitationException(e);
		}
	}

}
