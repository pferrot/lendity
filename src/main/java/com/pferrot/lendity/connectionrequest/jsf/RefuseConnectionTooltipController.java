package com.pferrot.lendity.connectionrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RefuseConnectionTooltipController extends AbstractConnectionResponseTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(RefuseConnectionTooltipController.class);

	@Override
	protected void process() {
		try {
			getConnectionRequestService().updateRefuseConnectionRequest(getConnectionRequestId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
