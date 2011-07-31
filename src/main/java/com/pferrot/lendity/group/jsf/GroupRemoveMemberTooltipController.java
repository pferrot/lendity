package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.group.exception.GroupException;

public class GroupRemoveMemberTooltipController extends AbstractGroupMemberTooltipController {
	
	private final static Log log = LogFactory.getLog(GroupRemoveMemberTooltipController.class);

	@Override
	protected void process() throws GroupException {
		getItemService().updateItemsRemoveGroupAuthorized(getPersonId(), getGroupId());
		getNeedService().updateNeedsRemoveGroupAuthorized(getPersonId(), getGroupId());
		getGroupService().updateGroupRemoveMember(getGroupId(), getPersonId());
	}
	
	
}
