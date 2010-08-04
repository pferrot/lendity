package com.pferrot.lendity.connectionrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyConnectionsUpdatesListController extends AbstractConnectionRequestsListController {

	private final static Log log = LogFactory.getLog(MyConnectionsUpdatesListController.class);
	
	
	public MyConnectionsUpdatesListController() {
		super();
		setRowsPerPage(20);
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getConnectionRequestService().findCurrentUserConnectionsUpdates(getFirstRow(), getRowsPerPage());
	}
}
