package com.pferrot.lendity.lendrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractLendResponseTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AbstractLendResponseTooltipController.class);
	
	private LendRequestService lendRequestService;
	
	private Long lendRequestId;
	
	// Not used for now - always redirects to the persons list.
	private Long redirectId;

	public LendRequestService getLendRequestService() {
		return lendRequestService;
	}

	public void setLendRequestService(LendRequestService lendRequestService) {
		this.lendRequestService = lendRequestService;
	}

	public Long getLendRequestId() {
		return lendRequestId;
	}

	public void setLendRequestId(Long lendRequestId) {
		this.lendRequestId = lendRequestId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		process();
		
		JsfUtils.redirect(PagesURL.MY_PENDING_LEND_REQUESTS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}
	
	protected abstract void process();
}
