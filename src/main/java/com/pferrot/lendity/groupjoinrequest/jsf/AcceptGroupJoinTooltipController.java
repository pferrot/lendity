package com.pferrot.lendity.groupjoinrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AcceptGroupJoinTooltipController extends AbstractGroupJoinResponseTooltipController {
	
	private final static Log log = LogFactory.getLog(AcceptGroupJoinTooltipController.class);

	@Override
	protected void process() {
		try {
			getGroupJoinRequestService().updateAcceptGroupJoinRequest(getGroupJoinRequestId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
