package com.pferrot.lendity.connectionrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;

public class BanConnectionTooltipController extends AbstractConnectionResponseTooltipController {
	
	private final static Log log = LogFactory.getLog(BanConnectionTooltipController.class);
	
	private Long personToBanId;
	

	public Long getPersonToBanId() {
		return personToBanId;
	}

	public void setPersonToBanId(Long personToBanId) {
		this.personToBanId = personToBanId;
	}

	@Override
	protected void process() {
		try {
			if (getConnectionRequestId() != null && getConnectionRequestId().longValue() > 0) {
				getConnectionRequestService().updateBanConnectionRequest(getConnectionRequestId());
			}
			else if (getPersonToBanId() != null && getPersonToBanId().longValue() > 0) {
				getConnectionRequestService().updateBanPerson(getPersonToBanId());
			}
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String submit() {
		process();
		
		if (getConnectionRequestId() != null && getConnectionRequestId().longValue() > 0) {
			JsfUtils.redirect(PagesURL.MY_PENDING_CONNECTION_REQUESTS_LIST);
		}
		else if (getPersonToBanId() != null && getPersonToBanId().longValue() > 0) {
			JsfUtils.redirect(PagesURL.PERSON_OVERVIEW, PagesURL.PERSON_OVERVIEW_PARAM_PERSON_ID, getPersonToBanId().toString());
		}
	
		// As a redirect is used, this is actually useless.
		return null;
	}
}
