package com.pferrot.lendity.lendrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.lendrequest.LendRequestService;

public class RequestLendTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RequestLendTooltipController.class);
	
	private LendRequestService LendRequestService;
	
	private Long itemId;
	
	// Not used for now - always redirects to the persons list.
	private Long redirectId;

	public  LendRequestService getLendRequestService() {
		return LendRequestService;
	}

	public void setLendRequestService(LendRequestService lendRequestService) {
		LendRequestService = lendRequestService;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		inviteAsFriend();
		
		JsfUtils.redirect(PagesURL.MY_CONNECTIONS_ITEMS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void inviteAsFriend() {
		try {
			getLendRequestService().createLendRequestFromCurrentUser(getItemId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
