package com.pferrot.lendity.lendtransaction.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.person.PersonUtils;


@ViewController(viewIds={"/auth/lendtransaction/myLendTransactionsForItemList.jspx"})
public class MyLendTransactionsForItemListController extends LendTransactionsForItemAndPersonListController {

	private final static Log log = LogFactory.getLog(MyLendTransactionsForItemListController.class);
	
	@Override
	public Long getPersonId() {
		return PersonUtils.getCurrentPersonId();
	}
	
	@Override
	protected String getPersonIdString() {
		return PersonUtils.getCurrentPersonId().toString();
	}
}
