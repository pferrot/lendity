package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.group.exception.GroupException;

public class GroupUnbanPersonTooltipController extends AbstractGroupMemberTooltipController {
	
	private final static Log log = LogFactory.getLog(GroupUnbanPersonTooltipController.class);

	@Override
	protected void process() throws GroupException {
		getGroupService().updateGroupUnbanPerson(getGroupId(), getPersonId());
	}
	
	
}
