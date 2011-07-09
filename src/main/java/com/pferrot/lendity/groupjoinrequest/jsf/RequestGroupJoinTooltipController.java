package com.pferrot.lendity.groupjoinrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestService;
import com.pferrot.lendity.utils.JsfUtils;

public class RequestGroupJoinTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RequestGroupJoinTooltipController.class);
	
	private GroupJoinRequestService groupJoinRequestService;
	
	private Long groupId;
	
	// 1 == groups list page
	// 2 == group overview page
	private Long redirectId;
	
	private String password;
	
	public GroupJoinRequestService getGroupJoinRequestService() {
		return groupJoinRequestService;
	}

	public void setGroupJoinRequestService(
			GroupJoinRequestService groupJoinRequestService) {
		this.groupJoinRequestService = groupJoinRequestService;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String submit() {
		requestGroupJoin();
		
		if (getRedirectId().longValue() == 1) {
			JsfUtils.redirect(PagesURL.GROUPS_LIST);
		}
		else if (getRedirectId().longValue() == 2) {
			JsfUtils.redirect(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, getGroupId().toString());
		}
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void requestGroupJoin() {
		try {
			getGroupJoinRequestService().createGroupJoinRequestFromCurrentUser(getGroupId(), getPassword());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
