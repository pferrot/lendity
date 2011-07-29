package com.pferrot.lendity.potentialconnection.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.potentialconnection.PotentialConnectionService;
import com.pferrot.lendity.utils.JsfUtils;

public class DeleteAllPotentialConnectionsTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(DeleteAllPotentialConnectionsTooltipController.class);
	
	private PotentialConnectionService potentialConnectionService;
	
	// 1 == my full potential connections list
	private Long redirectId;

	public PotentialConnectionService getPotentialConnectionService() {
		return potentialConnectionService;
	}

	public void setPotentialConnectionService(
			PotentialConnectionService potentialConnectionService) {
		this.potentialConnectionService = potentialConnectionService;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		deleteAllPotentialConnections();
		
		JsfUtils.redirect(PagesURL.MY_FULL_POTENTIAL_CONNECTIONS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void deleteAllPotentialConnections() {
		getPotentialConnectionService().deletePotentialConnectionsForPerson(PersonUtils.getCurrentPersonId());		
	}	
}
