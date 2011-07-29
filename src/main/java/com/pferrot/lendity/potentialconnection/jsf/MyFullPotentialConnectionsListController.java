package com.pferrot.lendity.potentialconnection.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyFullPotentialConnectionsListController extends AbstractPotentialConnectionsListController {

	private final static Log log = LogFactory.getLog(MyFullPotentialConnectionsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getPotentialConnectionService().findCurrentPersonFullPotentialConnectons(getFirstRow(), getRowsPerPage());
	}

}
