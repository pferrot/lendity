package com.pferrot.lendity.group.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/group/groupMembersList.jspx"})
public class GroupMembersListController extends AbstractGroupPersonsListController {
	
	private final static Log log = LogFactory.getLog(GroupMembersListController.class);

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getGroupService().findGroupMembers(getGroupId(), getFirstRow(), getRowsPerPage());
	}
	
	protected String getGroupIdString() {
		return JsfUtils.getRequestParameter(PagesURL.GROUP_MEMBERS_LIST_PARAM_GROUP_ID);
	}
}
