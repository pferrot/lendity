package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.person.PersonUtils;

public class MyGroupsListController extends AbstractGroupsListController {
	
	private final static Log log = LogFactory.getLog(MyGroupsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getGroupService().findPersonGroupsWhereOwnerOrAdministratorOrMember(PersonUtils.getCurrentPersonId(), getSearchString(), getFirstRow(), getRowsPerPage());
	}
}
