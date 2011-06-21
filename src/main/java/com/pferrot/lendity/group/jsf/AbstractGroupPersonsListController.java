package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.person.jsf.AbstractPersonsListController;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractGroupPersonsListController extends AbstractPersonsListController {
	
	private final static Log log = LogFactory.getLog(AbstractGroupPersonsListController.class);
	
	private GroupService groupService;
	
	private Long groupId;
	private String groupTitle;
	private Group group;
	
	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@InitView
	public void initView() {
		final String groupIdString = getGroupIdString();
		if (!StringUtils.isNullOrEmpty(groupIdString)) {
			setGroupId(Long.valueOf(groupIdString));
			setGroupTitle(getGroupService().findGroupTitle(getGroupId()));
			setGroup(getGroupService().findGroup(getGroupId()));
		}
	}
	
	protected abstract String getGroupIdString();
	
	
	public String getGroupUrl() {
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, getGroupId().toString());
	}
	
	public boolean isAuthorizedToRemoveMember() {
		final Person person = (Person)getTable().getRowData();
		return getGroupService().isUserAuthorizedToRemoveMember(getPersonService().getCurrentPerson(), getGroup(), person);
	}
	
	public boolean isAuthorizedToAddAdmin() {
		final Person person = (Person)getTable().getRowData();
		return getGroupService().isUserAuthorizedToAddAdmin(getPersonService().getCurrentPerson(), getGroup(), person);
	}
	
	public boolean isAuthorizedToBanMember() {
		final Person person = (Person)getTable().getRowData();
		return getGroupService().isUserAuthorizedToBanPerson(getPersonService().getCurrentPerson(), getGroup(), person);
	}
}
