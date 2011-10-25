package com.pferrot.lendity.lendtransaction.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/lendtransaction/myLendTransactionsOutWaitingForInputList.jspx"})
public class MyLendTransactionsOutWaitingForInputListController extends AbstractMyLendTransactionsWaitingForInputListController {

	private final static Log log = LogFactory.getLog(MyLendTransactionsOutWaitingForInputListController.class);

	@Override
	protected List getListInternal() {
		return getLendTransactionService().findCurrentPersonLendTransactionsAsBorrowerWaitingForInputList();
	}

	
}
