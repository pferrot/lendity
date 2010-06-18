package com.pferrot.lendity.lendrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RefuseLendTooltipController extends AbstractLendResponseTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RefuseLendTooltipController.class);

	@Override
	protected void process() {
		try {
			getLendRequestService().updateRefuseLendRequest(getLendRequestId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
