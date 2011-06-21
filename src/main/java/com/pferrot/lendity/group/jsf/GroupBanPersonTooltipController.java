package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.group.exception.GroupException;

public class GroupBanPersonTooltipController extends AbstractGroupMemberTooltipController {
	
	private final static Log log = LogFactory.getLog(GroupBanPersonTooltipController.class);

	@Override
	protected void process() throws GroupException {
		getGroupService().updateGroupBanPerson(getGroupId(), getPersonId());
	}
	
	
}
