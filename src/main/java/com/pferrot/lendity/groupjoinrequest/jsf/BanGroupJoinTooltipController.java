package com.pferrot.lendity.groupjoinrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BanGroupJoinTooltipController extends AbstractGroupJoinResponseTooltipController {
	
	private final static Log log = LogFactory.getLog(BanGroupJoinTooltipController.class);

	@Override
	protected void process() {
		try {
			getGroupJoinRequestService().updateBanGroupJoinRequest(getGroupJoinRequestId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
