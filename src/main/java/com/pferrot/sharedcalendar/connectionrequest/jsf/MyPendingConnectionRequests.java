package com.pferrot.sharedcalendar.connectionrequest.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.sharedcalendar.model.ConnectionRequest;

public class MyPendingConnectionRequests extends AbstractConnectionRequestsListController {

	private final static Log log = LogFactory.getLog(MyPendingConnectionRequests.class);
	
	@Override
	public List getListInternal() {
		return getConnectionRequestService().findCurrentUserPendingConnectionRequests(getFirstResultIndex(), getNbEntriesPerPage() + 1);
	}

	public String acceptConnectionRequest() {
		try {
			final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
			getConnectionRequestService().updateAcceptConnectionRequest(connectionRequest);
			return "requestConnection";
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public String getAcceptConnectionRequestLabel() {
		return "accept";
	}

	public boolean getAcceptConnectionRequestDisabled() {
		return false;		
	}

	public String refuseConnectionRequest() {
		try {
			final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
			getConnectionRequestService().updateRefuseConnectionRequest(connectionRequest);
			return "requestConnection";
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public String getRefuseConnectionRequestLabel() {
		return "refuse";
	}

	public boolean getRefuseConnectionRequestDisabled() {
		return false;		
	}

	public String banConnectionRequest() {
		try {
			final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
			getConnectionRequestService().updateBanConnectionRequest(connectionRequest);
			return "requestConnection";
		}
		catch (ConnectionRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public String getBanConnectionRequestLabel() {
		return "ban";
	}

	public boolean getBanConnectionRequestDisabled() {
		return false;		
	}

}
