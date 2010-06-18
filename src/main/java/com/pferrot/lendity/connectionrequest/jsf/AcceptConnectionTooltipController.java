package com.pferrot.lendity.connectionrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AcceptConnectionTooltipController extends AbstractConnectionResponseTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AcceptConnectionTooltipController.class);

	@Override
	protected void process() {
		try {
			getConnectionRequestService().updateAcceptConnectionRequest(getConnectionRequestId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
