package com.pferrot.lendity.lendtransaction.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/lendtransaction/myLendTransactionsList.jspx"})
public class MyLendTransactionsListController extends AbstractMyLendTransactionsListController {

	private final static Log log = LogFactory.getLog(MyLendTransactionsListController.class);

	@Override
	protected List getFutureListInternal() {
		return getLendTransactionService().findCurrentUserFutureLendTransactionsAsLenderList();
	}

	@Override
	public List getToEvaluateListInternal() {
		return getLendTransactionService().findCurrentUserToEvaluateLendTransactionsAsLenderList();
	}

	@Override
	protected List getInProgressListInternal() {
		return getLendTransactionService().findCurrentUserInProgressLendTransactionsAsLenderList();
	}
}
