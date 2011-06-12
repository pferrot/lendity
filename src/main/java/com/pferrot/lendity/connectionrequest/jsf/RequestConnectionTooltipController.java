package com.pferrot.lendity.connectionrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

public class RequestConnectionTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RequestConnectionTooltipController.class);
	
	private ConnectionRequestService connectionRequestService;
	
	private Long personId;
	
	// 1 == persons list page
	// 2 == person overview page
	private Long redirectId;
	
	private String text;
	
	

	public RequestConnectionTooltipController() {
		final String firstName = PersonUtils.getCurrentPersonFirstName();
		final String lastName = PersonUtils.getCurrentPersonLastName();
		
		text = I18nUtils.getMessageResourceString("connectionRequest_textDefaultValue",
												  new Object[]{firstName, lastName},
												  I18nUtils.getDefaultLocale());
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	
	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = StringUtils.getNullIfEmpty(text);
	}

	public String submit() {
		inviteAsFriend();
		
		if (getRedirectId().longValue() == 1) {
			JsfUtils.redirect(PagesURL.PERSONS_LIST);
		}
		else if (getRedirectId().longValue() == 2) {
			JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, getPersonId().toString());
		}
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void inviteAsFriend() {
		try {
			getConnectionRequestService().createConnectionRequestFromCurrentUser(getPersonId(), getText());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
