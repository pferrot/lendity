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
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

@ViewController(viewIds={"/public/item/search.jspx"})
public class SearchItemsListController extends AbstractItemsWithOwnerListController {
	
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
			setOrderBy(new Long(2));
			setSearchString(null);
			setCategoryId(null);			
			setBorrowStatus(null);
			setVisibilityId(null);
			setOwnerId(null);
			return;
		}
		// Note the return above...
		final String searchString = JsfUtils.getRequestParameter(SEARCH_TEXT_PARAM_NAME);
		if (!StringUtils.isNullOrEmpty(searchString)) {
			setOrderBy(new Long(1));
			setSearchString(searchString);
			setCategoryId(null);			
			setBorrowStatus(null);
			setVisibilityId(null);
			setOwnerId(null);
			return;
		}		
	}

	@Override
	protected ListWithRowCount getListWithRowCount() {
		Double maxDistanceDouble = null;
	    if (getMaxDistance() != null) {
	    	maxDistanceDouble = Double.valueOf(getMaxDistance());
	    }
		return getItemService().findMyConnectionsItems(getOwnerId(), getSearchString(), getCategoryId(), 
				getBorrowStatusBoolean(), getShowOnlyConnectionsItems(), maxDistanceDouble, getOrderByField(), getOrderByAscending(), getFirstRow(), getRowsPerPage());
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

	@Override
	public String getOwnerLabel() {
		if (!isOwnerNameAvailable()) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("item_ownerNotAvailable", locale);
		}
		return super.getOwnerLabel();
	}

	/**
	 * The name of the owner is available if the current user has RW access or if he is a connection. I.e. users
	 * cannot see the owner of public items belonging to users they do not know.
	 * 
	 * @return
	 */
	public boolean isOwnerNameAvailable() {
		final InternalItem item = (InternalItem)getTable().getRowData();
		return getItemService().isCurrentUserAuthorizedToViewOwnerName(item);
	}

	public boolean isSearchByDistanceAvailable() {
		return PersonUtils.isCurrentPersonIsAddressDefined();
	}
}