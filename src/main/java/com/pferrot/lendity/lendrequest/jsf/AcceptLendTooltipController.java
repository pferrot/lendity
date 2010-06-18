package com.pferrot.lendity.lendrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AcceptLendTooltipController extends AbstractLendResponseTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AcceptLendTooltipController.class);

	@Override
	protected void process() {
		try {
			getLendRequestService().updateAcceptLendRequest(getLendRequestId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
