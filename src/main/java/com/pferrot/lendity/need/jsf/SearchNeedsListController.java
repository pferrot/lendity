package com.pferrot.lendity.need.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/public/need/searchNeeds.jspx"})
public class SearchNeedsListController extends AbstractNeedsListController {
	
	private final static Log log = LogFactory.getLog(SearchNeedsListController.class);

	public final static String FORCE_VIEW_PARAM_NAME = "view";
	public final static String FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE = "allByCr";
	
	public final static String SEARCH_TEXT_PARAM_NAME = "search";
	
	public SearchNeedsListController() {
		super();
		// Display available items by default.
		//setBorrowStatus(UiUtils.getLongFromBoolean(Boolean.FALSE));
	}

	@InitView
	public void initView() {		
		final String orderBy = JsfUtils.getRequestParameter(FORCE_VIEW_PARAM_NAME);
		if (FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE.equals(orderBy)) {
			resetFilters();
			setOrderBy(new Long(2));
			return;
		}
		// Note the return above...
		final String searchString = JsfUtils.getRequestParameter(SEARCH_TEXT_PARAM_NAME);
		if (!StringUtils.isNullOrEmpty(searchString)) {
			resetFilters();
			setSearchString(searchString);
			return;
		}
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		Double maxDistanceDouble = null;
	    if (getMaxDistance() != null) {
	    	maxDistanceDouble = Double.valueOf(getMaxDistance());
	    }
		return getNeedService().findNeeds(getSearchString(), getCategoryId(), 
				getOwnerType(), maxDistanceDouble, getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}
}