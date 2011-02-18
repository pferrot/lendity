package com.pferrot.lendity.lendtransaction.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyInProgressLendTransactionsListController extends AbstractLendTransactionsListController {

	private final static Log log = LogFactory.getLog(MyInProgressLendTransactionsListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getLendTransactionService().findCurrentUserInProgressLendTransactionsAsLender(getFirstRow(), getRowsPerPage());
	}
}
