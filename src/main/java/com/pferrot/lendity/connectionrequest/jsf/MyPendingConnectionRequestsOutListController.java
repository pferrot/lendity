package com.pferrot.lendity.connectionrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyPendingConnectionRequestsOutListController extends AbstractConnectionRequestsListController {

	private final static Log log = LogFactory.getLog(MyPendingConnectionRequestsOutListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getConnectionRequestService().findCurrentUserPendingConnectionRequestsOut(getFirstRow(), getRowsPerPage());
	}
}
