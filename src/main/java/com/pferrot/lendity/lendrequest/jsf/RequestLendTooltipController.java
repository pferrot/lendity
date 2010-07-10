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
	
	// 1 == my connections items page
	// 2 == item overview page
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
		requestLend();
		
		if (getRedirectId().longValue() == 1) {
			JsfUtils.redirect(PagesURL.MY_CONNECTIONS_ITEMS_LIST);
		}
		else if (getRedirectId().longValue() == 2) {
			JsfUtils.redirect(PagesURL.INTERNAL_ITEM_OVERVIEW, PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID, getItemId().toString());
		}
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void requestLend() {
		try {
			getLendRequestService().createLendRequestFromCurrentUser(getItemId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
