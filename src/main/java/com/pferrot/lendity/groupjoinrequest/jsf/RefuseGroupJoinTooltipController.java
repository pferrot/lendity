package com.pferrot.lendity.groupjoinrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RefuseGroupJoinTooltipController extends AbstractGroupJoinResponseTooltipController {
	
	private final static Log log = LogFactory.getLog(RefuseGroupJoinTooltipController.class);

	@Override
	protected void process() {
		try {
			getGroupJoinRequestService().updateRefuseGroupJoinRequest(getGroupJoinRequestId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
