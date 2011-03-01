package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/public/item/searchItems.jspx"})
public class SearchItemsListController extends AbstractItemsListController {
	
	private final static Log log = LogFactory.getLog(SearchItemsListController.class);
	
	public final static String FORCE_VIEW_PARAM_NAME = "view";
	public final static String FORCE_VIEW_ALL_BY_CREATION_DATE_VALUE = "allByCr";
	
	public final static String SEARCH_TEXT_PARAM_NAME = "search";	
	
	public SearchItemsListController() {
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
		return getItemService().findItems(getSearchString(), getCategoryId(), 
				getBorrowStatusBoolean(), getOwnerType(), maxDistanceDouble, getLendType(), getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
	}

	@Override
	public List<SelectItem> getBorrowStatusSelectItems() {
		if (getBorrowStatusSelectItemsInternal() == null) {
			final List<SelectItem> result = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			
			result.add(new SelectItem(UiUtils.getLongFromBoolean(null), I18nUtils.getMessageResourceString("item_availableStatusAll", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.FALSE), I18nUtils.getMessageResourceString("item_availableStatusAvailable", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.TRUE), I18nUtils.getMessageResourceString("item_availableStatusNotAvailable", locale)));
			
			setBorrowStatusSelectItemsInternal(result);
		}		
		return getBorrowStatusSelectItemsInternal();	
	}
}