package com.pferrot.lendity.groupjoinrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestService;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractGroupJoinResponseTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AbstractGroupJoinResponseTooltipController.class);
	
	private GroupJoinRequestService groupJoinRequestService;
	
	private Long groupJoinRequestId;
	
	// Not used for now - always redirects to the requests list.
	private Long redirectId;

	public GroupJoinRequestService getGroupJoinRequestService() {
		return groupJoinRequestService;
	}

	public void setGroupJoinRequestService(
			GroupJoinRequestService groupJoinRequestService) {
		this.groupJoinRequestService = groupJoinRequestService;
	}

	public Long getGroupJoinRequestId() {
		return groupJoinRequestId;
	}

	public void setGroupJoinRequestId(Long groupJoinRequestId) {
		this.groupJoinRequestId = groupJoinRequestId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		process();
		
		JsfUtils.redirect(PagesURL.MY_PENDING_GROUP_JOIN_REQUESTS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}
	
	protected abstract void process();
}
