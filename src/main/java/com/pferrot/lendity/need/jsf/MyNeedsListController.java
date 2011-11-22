package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/need/myNeedsList.jspx"})
public class MyNeedsListController extends AbstractNeedsListController {
	
	private final static Log log = LogFactory.getLog(MyNeedsListController.class);
	
	public MyNeedsListController() {
		super();
	}
	
	@InitView
	public void initView() {		
		super.initView();
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		return getNeedService().findMyNeeds(getSearchString(), getCategoryId(), getVisibilityId(), getFulfilledBoolean(),
				getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}
	
	@Override
	protected String getCategoryLinkBaseUrl() {
		return JsfUtils.getFullUrl(PagesURL.MY_NEEDS_LIST, PagesURL.NEEDS_SEARCH_PARAM_CATEGORY_ID, PagesURL.NEEDS_SEARCH_PARAM_CATEGORY_ID_TO_REPLACE);
	}
	
	
}
