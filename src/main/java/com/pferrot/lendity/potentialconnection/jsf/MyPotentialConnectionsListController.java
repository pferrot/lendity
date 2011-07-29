package com.pferrot.lendity.potentialconnection.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyPotentialConnectionsListController extends AbstractPotentialConnectionsListController {

	private final static Log log = LogFactory.getLog(MyPotentialConnectionsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getPotentialConnectionService().findCurrentPersonPotentialConnectons(getFirstRow(), getRowsPerPage());
	}

}
