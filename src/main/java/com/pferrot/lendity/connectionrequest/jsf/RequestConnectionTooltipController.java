package com.pferrot.lendity.connectionrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.utils.JsfUtils;

public class RequestConnectionTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RequestConnectionTooltipController.class);
	
	private ConnectionRequestService connectionRequestService;
	
	private Long personId;
	
	// Not used for now - always redirects to the persons list.
	private Long redirectId;

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

	public String submit() {
		inviteAsFriend();
		
		JsfUtils.redirect(PagesURL.PERSONS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void inviteAsFriend() {
		try {
			getConnectionRequestService().createConnectionRequestFromCurrentUser(getPersonId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
