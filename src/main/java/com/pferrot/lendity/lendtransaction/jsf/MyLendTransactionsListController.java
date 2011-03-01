package com.pferrot.lendity.lendtransaction.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.person.PersonUtils;

@ViewController(viewIds={"/auth/lendtransaction/myLendTransactionsList.jspx"})
public class MyLendTransactionsListController extends AbstractLendTransactionsListController {

	private final static Log log = LogFactory.getLog(MyLendTransactionsListController.class);

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getLendTransactionService().findLendTransactionsForItemAndPerson(null, PersonUtils.getCurrentPersonId(), getStatusId(), getFirstRow(), getRowsPerPage());
	}
}
