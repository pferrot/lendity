package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.group.exception.GroupException;

public class GroupRemoveAdminTooltipController extends AbstractGroupMemberTooltipController {
	
	private final static Log log = LogFactory.getLog(GroupRemoveAdminTooltipController.class);

	@Override
	protected void process() throws GroupException {
		getGroupService().updateGroupRemoveAdmin(getGroupId(), getPersonId());
		// Remove admin, but keep as normal member.
		getGroupService().updateGroupAddMember(getGroupId(), getPersonId());
	}
	
	
}
