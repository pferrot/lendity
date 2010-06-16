package com.pferrot.lendity.lendrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyPendingLendRequestsOutListController extends AbstractLendRequestsListController {

	private final static Log log = LogFactory.getLog(MyPendingLendRequestsOutListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getLendRequestService().findCurrentUserPendingLendRequestsOut(getFirstRow(), getRowsPerPage());
	}
}
