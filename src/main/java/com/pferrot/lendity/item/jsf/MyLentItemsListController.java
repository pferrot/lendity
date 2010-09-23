package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

@ViewController(viewIds={"/auth/item/myLentItemsList.jspx"})
public class MyLentItemsListController extends MyItemsListController {
	
	private final static Log log = LogFactory.getLog(MyLentItemsListController.class);
	
	
	public MyLentItemsListController() {
		super();
		setBorrowStatus(new Long(1));
	}	
}
