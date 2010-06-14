package com.pferrot.lendity.connectionrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ConnectionRequest;

public class MyPendingConnectionRequestsListController extends AbstractConnectionRequestsListController {

	private final static Log log = LogFactory.getLog(MyPendingConnectionRequestsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getConnectionRequestService().findCurrentUserPendingConnectionRequests(getFirstRow(), getRowsPerPage());
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

}
