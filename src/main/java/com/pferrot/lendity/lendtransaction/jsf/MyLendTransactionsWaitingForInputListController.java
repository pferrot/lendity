package com.pferrot.lendity.lendtransaction.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/lendtransaction/myLendTransactionsWaitingForInputList.jspx"})
public class MyLendTransactionsWaitingForInputListController extends AbstractMyLendTransactionsWaitingForInputListController {

	private final static Log log = LogFactory.getLog(MyLendTransactionsWaitingForInputListController.class);

	@Override
	protected List getListInternal() {
		return getLendTransactionService().findCurrentPersonLendTransactionsAsLenderWaitingForInputList();
	}

	
}
