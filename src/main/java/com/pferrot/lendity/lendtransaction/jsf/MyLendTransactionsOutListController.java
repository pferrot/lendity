package com.pferrot.lendity.lendtransaction.jsf;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/lendtransaction/myLendTransactionsOutList.jspx"})
public class MyLendTransactionsOutListController extends AbstractMyLendTransactionsListController {

	private final static Log log = LogFactory.getLog(MyLendTransactionsOutListController.class);

	@Override
	protected List getFutureListInternal() {
		return getLendTransactionService().findCurrentUserFutureLendTransactionsAsBorrowerList();
	}

	@Override
	public List getToEvaluateListInternal() {
		return getLendTransactionService().findCurrentUserToEvaluateLendTransactionsAsBorrowerList();
	}

	@Override
	protected List getInProgressListInternal() {
		return getLendTransactionService().findCurrentUserInProgressLendTransactionsAsBorrowerList();
	}
}
