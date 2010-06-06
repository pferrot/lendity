package com.pferrot.sharedcalendar.connectionrequest.jsf;

import com.pferrot.sharedcalendar.connectionrequest.ConnectionRequestConsts;
import com.pferrot.sharedcalendar.connectionrequest.ConnectionRequestService;
import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.ConnectionRequest;
import com.pferrot.sharedcalendar.person.PersonUtils;

public abstract class AbstractConnectionRequestsListController extends AbstractListController {
	
	private ConnectionRequestService connectionRequestService;

	
	public AbstractConnectionRequestsListController() {
		super();
		setRowsPerPage(ConnectionRequestConsts.NB_CONNECTIONS_REQUESTS_PER_PAGE);
	}

	public ConnectionRequestService getConnectionRequestService() {
		return connectionRequestService;
	}

	public void setConnectionRequestService(final ConnectionRequestService pConnectionRequestService) {
		this.connectionRequestService = pConnectionRequestService;
	}

	public String getRequesterOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getRequester().getId().toString());
	}

	public String getConnectionOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getConnection().getId().toString());
	}
}
