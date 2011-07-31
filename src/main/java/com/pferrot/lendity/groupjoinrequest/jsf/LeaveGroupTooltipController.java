package com.pferrot.lendity.groupjoinrequest.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

public class LeaveGroupTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(LeaveGroupTooltipController.class);
	
	private GroupService groupService;
	private ItemService itemService;
	private NeedService needService;
	
	private Long groupId;
	
	// 1 == groups list page
	// 2 == group overview page
	private Long redirectId;
	
	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public NeedService getNeedService() {
		return needService;
	}

	public void setNeedService(NeedService needService) {
		this.needService = needService;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		leaveGroup();
		
		if (getRedirectId().longValue() == 1) {
			JsfUtils.redirect(PagesURL.GROUPS_LIST);
		}
		else if (getRedirectId().longValue() == 2) {
			JsfUtils.redirect(PagesURL.GROUP_OVERVIEW, PagesURL.GROUP_OVERVIEW_PARAM_GROUP_ID, getGroupId().toString());
		}
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void leaveGroup() {
		try {
			getItemService().updateItemsRemoveGroupAuthorized(PersonUtils.getCurrentPersonId(), getGroupId());
			getNeedService().updateNeedsRemoveGroupAuthorized(PersonUtils.getCurrentPersonId(), getGroupId());
			getGroupService().updateGroupRemoveCurrentUserFromMembersAndAdministrators(getGroupId());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
