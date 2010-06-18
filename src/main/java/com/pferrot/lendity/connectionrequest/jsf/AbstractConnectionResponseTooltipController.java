package com.pferrot.lendity.connectionrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractConnectionResponseTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AbstractConnectionResponseTooltipController.class);
	
	private ConnectionRequestService connectionRequestService;
	
	private Long connectionRequestId;
	
	// Not used for now - always redirects to the persons list.
	private Long redirectId;

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(
			ConnectionRequestService connectionRequestService) {
		this.connectionRequestService = connectionRequestService;
	}
	
	public Long getConnectionRequestId() {
		return connectionRequestId;
	}

	public void setConnectionRequestId(Long connectionRequestId) {
		this.connectionRequestId = connectionRequestId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		process();
		
		JsfUtils.redirect(PagesURL.MY_PENDING_CONNECTION_REQUESTS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}
	
	protected abstract void process();
}
