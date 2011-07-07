package com.pferrot.lendity.connectionrequest.jsf;

import com.pferrot.lendity.connectionrequest.ConnectionRequestConsts;
import com.pferrot.lendity.connectionrequest.ConnectionRequestService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.ConnectionRequest;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractConnectionRequestsListController extends AbstractListController {
	
	private ConnectionRequestService connectionRequestService;
	private PersonService personService;

	
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

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public String getRequesterOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getRequester().getId().toString());
	}

	public String getConnectionOverviewHref() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(connectionRequest.getConnection().getId().toString());
	}

	public String getRequestDateLabel() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return UiUtils.getDateAsString(connectionRequest.getRequestDate(), I18nUtils.getDefaultLocale());
	}
	
	public String getResponseDateLabel() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return UiUtils.getDateAsString(connectionRequest.getResponseDate(), I18nUtils.getDefaultLocale());
	}

	public String getRequesterThumbnailSrc() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return personService.getProfileThumbnailSrc(connectionRequest.getRequester(), true);
	}

	public String getConnectionThumbnailSrc() {
		final ConnectionRequest connectionRequest = (ConnectionRequest)getTable().getRowData();
		return personService.getProfileThumbnailSrc(connectionRequest.getConnection(), true);
	}
}
