package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class GroupsListController extends AbstractGroupsListController {
	
	private final static Log log = LogFactory.getLog(GroupsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getGroupService().findGroups (getSearchString(), getFirstRow(), getRowsPerPage());
	}
}
