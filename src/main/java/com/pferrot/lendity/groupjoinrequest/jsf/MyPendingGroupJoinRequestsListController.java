package com.pferrot.lendity.groupjoinrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.groupjoinrequest.exception.GroupJoinRequestException;
import com.pferrot.lendity.model.GroupJoinRequest;

public class MyPendingGroupJoinRequestsListController extends AbstractGroupJoinRequestsListController {

	private final static Log log = LogFactory.getLog(MyPendingGroupJoinRequestsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getGroupJoinRequestService().findCurrentUserPendingGroupJoinRequests(getFirstRow(), getRowsPerPage());
	}

	public String acceptGroupJoinRequest() {
		try {
			final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
			getGroupJoinRequestService().updateAcceptGroupJoinRequest(groupJoinRequest);
			return "acceptGroupJoinRequest";
		}
		catch (GroupJoinRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public String refuseGroupJoinRequest() {
		try {
			final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
			getGroupJoinRequestService().updateRefuseGroupJoinRequest(groupJoinRequest);
			return "refuseGroupJoinRequest";
		}
		catch (GroupJoinRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public String banGroupJoinRequest() {
		try {
			final GroupJoinRequest groupJoinRequest = (GroupJoinRequest)getTable().getRowData();
			getGroupJoinRequestService().updateBanGroupJoinRequest(groupJoinRequest);
			return "banGroupJoinRequest";
		}
		catch (GroupJoinRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

}
