package com.pferrot.lendity.connectionrequest.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

@ViewController(viewIds={"/auth/connectionrequest/myConnectionsUpdatesList.jspx"})
public class MyConnectionsUpdatesListController extends AbstractConnectionRequestsListController {

	private final static Log log = LogFactory.getLog(MyConnectionsUpdatesListController.class);
	
	
	public MyConnectionsUpdatesListController() {
		super();
		setRowsPerPage(20);
	}
	
	@InitView
	public void initView() {
		// Force first page.
		page(0);
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getConnectionRequestService().findCurrentUserConnectionsUpdates(getFirstRow(), getRowsPerPage());
	}
}
