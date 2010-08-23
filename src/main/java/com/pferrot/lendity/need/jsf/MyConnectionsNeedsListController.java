package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/need/myConnectionsNeedsList.jspx"})
public class MyConnectionsNeedsListController extends AbstractNeedsWithOwnerListController {
	
	private final static Log log = LogFactory.getLog(MyConnectionsNeedsListController.class);

	public final static String FORCE_VIEW_PARAM_NAME = "view";
	public final static String FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE = "allByCr";
	
	public MyConnectionsNeedsListController() {
		super();
		// Display available items by default.
		//setBorrowStatus(UiUtils.getLongFromBoolean(Boolean.FALSE));
	}

	@InitView
	public void initView() {		
		final String orderBy = JsfUtils.getRequestParameter(FORCE_VIEW_PARAM_NAME);
		if (FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE.equals(orderBy)) {
			setOrderBy(new Long(2));
			setSearchString(null);
			setCategoryId(null);			
			setOwnerId(null);
		}
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getNeedService().findMyConnectionsNeeds(getOwnerId(), getSearchString(), getCategoryId(), 
				getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}
}