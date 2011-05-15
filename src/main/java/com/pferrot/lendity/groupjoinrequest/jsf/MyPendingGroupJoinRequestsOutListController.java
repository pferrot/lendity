package com.pferrot.lendity.groupjoinrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyPendingGroupJoinRequestsOutListController extends AbstractGroupJoinRequestsListController {

	private final static Log log = LogFactory.getLog(MyPendingGroupJoinRequestsOutListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getGroupJoinRequestService().findCurrentUserPendingGroupJoinRequestsOut(getFirstRow(), getRowsPerPage());
	}
}
