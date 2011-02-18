package com.pferrot.lendity.lendtransaction.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

public class MyInProgressLendTransactionsOutListController extends AbstractLendTransactionsListController {

	private final static Log log = LogFactory.getLog(MyInProgressLendTransactionsOutListController.class);
	
	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getLendTransactionService().findCurrentUserInProgressLendTransactionsAsBorrower(getFirstRow(), getRowsPerPage());
	}
}
