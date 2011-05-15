package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.person.PersonUtils;

public class MyGroupsWhereOwnerOrAdministratorListController extends AbstractGroupsListController {
	
	private final static Log log = LogFactory.getLog(MyGroupsWhereOwnerOrAdministratorListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getGroupService().findPersonGroupsWhereOwnerOrAdministrator(PersonUtils.getCurrentPersonId(), getSearchString(), getFirstRow(), getRowsPerPage());
	}
}
