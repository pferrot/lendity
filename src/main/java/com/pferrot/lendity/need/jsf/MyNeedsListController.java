package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.dao.bean.ListWithRowCount;

@ViewController(viewIds={"/auth/need/myNeedsList.jspx"})
public class MyNeedsListController extends AbstractNeedsListController {
	
	private final static Log log = LogFactory.getLog(MyNeedsListController.class);
	
	public MyNeedsListController() {
		super();
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getNeedService().findMyNeeds(getSearchString(), getCategoryId(), getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}
	

	
}
