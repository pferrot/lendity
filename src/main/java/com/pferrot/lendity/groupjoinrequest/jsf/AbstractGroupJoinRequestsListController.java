package com.pferrot.lendity.groupjoinrequest.jsf;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestConsts;
import com.pferrot.lendity.groupjoinrequest.GroupJoinRequestService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.GroupJoinRequest;
import com.pferrot.lendity.person.PersonService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractGroupJoinRequestsListController extends AbstractListController {
	
	private GroupJoinRequestService groupJoinRequestService;
	private PersonService personService;

	
	public AbstractGroupJoinRequestsListController() {
		super();
		setRowsPerPage(GroupJoinRequestConsts.NB_GROUP_JOIN_REQUESTS_PER_PAGE);
	}

	public GroupJoinRequestService getGroupJoinRequestService() {
		return groupJoinRequestService;
	}

	public void setGroupJoinRequestService(
			GroupJoinRequestService groupJoinRequestService) {
		this.groupJoinRequestService = groupJoinRequestService;
	}
	
	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public String getRequesterOverviewHref() {
		final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(groupJoinRequest.getRequester().getId().toString());
	}

	public String getGroupOverviewHref() {
		final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, groupJoinRequest.getGroup().getId().toString());
	}

	public String getRequestDateLabel() {
		final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
		return UiUtils.getDateAsString(groupJoinRequest.getRequestDate(), I18nUtils.getDefaultLocale());
	}
	
	public String getResponseDateLabel() {
		final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
		return UiUtils.getDateAsString(groupJoinRequest.getResponseDate(), I18nUtils.getDefaultLocale());
	}

	public String getRequesterThumbnailSrc() {
		final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
		return personService.getProfileThumbnailSrc(groupJoinRequest.getRequester(), true);
	}
}
