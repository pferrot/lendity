package com.pferrot.lendity.group.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.group.GroupService;
import com.pferrot.lendity.group.exception.GroupException;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.need.NeedService;
import com.pferrot.lendity.utils.JsfUtils;

public abstract class AbstractGroupMemberTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(AbstractGroupMemberTooltipController.class);
	
	private GroupService groupService;
	private ItemService itemService;
	private NeedService needService;
	
	private Long personId;
	
	private Long groupId;	
	
	// 1 == group admins page
	// 2 == group members page
	// 3 == group banned members page
	private Long redirectId;	

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}
	
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
		try {
			process();
			
			if (getRedirectId().longValue() == 1) {
				JsfUtils.redirect(PagesURL.GROUP_ADMINISTRATORS_LIST, PagesURL.GROUP_ADMINISTRATORS_LIST_PARAM_GROUP_ID, getGroupId().toString());
			}
			else if (getRedirectId().longValue() == 2) {
				JsfUtils.redirect(PagesURL.GROUP_MEMBERS_LIST, PagesURL.GROUP_MEMBERS_LIST_PARAM_GROUP_ID, getGroupId().toString());
			}
			else if (getRedirectId().longValue() == 3) {
				JsfUtils.redirect(PagesURL.GROUP_BANNED_LIST, PagesURL.GROUP_BANNED_LIST_PARAM_GROUP_ID, getGroupId().toString());
			}
		
			// As a redirect is used, this is actually useless.
			return null;
		}
		catch (GroupException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract void process() throws GroupException;
}
