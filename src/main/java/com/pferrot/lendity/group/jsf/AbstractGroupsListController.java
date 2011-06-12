package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupConsts;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.Person;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractGroupsListController extends AbstractListController {
	
	private final static Log log = LogFactory.getLog(AbstractGroupsListController.class);
	
	private GroupService groupService;

	public AbstractGroupsListController() {
		super();
		setRowsPerPage(GroupConsts.NB_GROUPS_PER_PAGE);
	}

	public GroupService getGroupService() {
		return groupService;
	}
	
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}
	
	public String getGroupOverviewHref() {
		final Group group = (Group)getTable().getRowData();
		return JsfUtils.getFullUrl(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, group.getId().toString());
	}
	
	public boolean isFilteredList() {
		return !StringUtils.isNullOrEmpty(getSearchString());
	}

	public String getGroupPicture1Src() {
		final Group group = (Group)getTable().getRowData();
		return groupService.getGroupPicture1Src(group, true);
	}
	
	public String getGroupThumbnail1Src() {
		final Group group = (Group)getTable().getRowData();
		return groupService.getGroupThumbnail1Src(group, true);
	}
}
