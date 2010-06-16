package com.pferrot.lendity.lendrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.connectionrequest.exception.ConnectionRequestException;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.lendrequest.exception.LendRequestException;
import com.pferrot.lendity.model.ConnectionRequest;
import com.pferrot.lendity.model.LendRequest;

public class MyPendingLendRequestsListController extends AbstractLendRequestsListController {

	private final static Log log = LogFactory.getLog(MyPendingLendRequestsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getLendRequestService().findCurrentUserPendingLendRequests(getFirstRow(), getRowsPerPage());
	}

	public String acceptLendRequest() {
		try {
			final LendRequest lendRequest = (LendRequest)getTable().getRowData();
			getLendRequestService().updateAcceptLendRequest(lendRequest);
			return "acceptLendRequest";
		}
		catch (LendRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}

	public String refuseLendRequest() {
		try {
			final LendRequest lendRequest = (LendRequest)getTable().getRowData();
			getLendRequestService().updateRefuseLendRequest(lendRequest);
			return "refuseLendRequest";
		}
		catch (LendRequestException e) {
			// TODO redirect to error page instead.
			throw new RuntimeException(e);
		}		
	}
}
