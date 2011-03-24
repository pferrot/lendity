package com.pferrot.lendity.lendtransaction.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

@ViewController(viewIds={"/auth/lendtransaction/myLendTransactionsWaitingForInputList.jspx"})
public class MyLendTransactionsWaitingForInputListController extends AbstractLendTransactionsListController {

	private final static Log log = LogFactory.getLog(MyLendTransactionsWaitingForInputListController.class);

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getLendTransactionService().findLendTransactionsWaitingForInputForCurrentPerson(getFirstRow(), getRowsPerPage());
	}
}
