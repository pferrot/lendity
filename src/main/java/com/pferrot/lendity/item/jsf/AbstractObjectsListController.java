package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemConsts;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.CategoryEnabled;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractObjectsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractObjectsListController.class);
	
	private ItemService itemService;

	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	
	private List<SelectItem> orderBySelectItems;
	// 1 = by title ascending
	// 2 = by creationDate descending
	private Long orderBy;
	
	
	public AbstractObjectsListController() {
		super();
		setRowsPerPage(ItemConsts.NB_ITEMS_PER_PAGE);
		setOrderBy(new Long(1));
	}

	public void setItemService(final ItemService pItemService) {
		this.itemService = pItemService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}

	public List<SelectItem> getCategoriesSelectItems() {
		if (categoriesSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			categoriesSelectItems = UiUtils.getSelectItemsForListValue(itemService.getCategories(), locale);
			// Add all categories first.
			categoriesSelectItems.add(0, getAllCategoriesSelectItem(locale));
		}		
		return categoriesSelectItems;	
	}

	private SelectItem getAllCategoriesSelectItem(final Locale pLocale) {
		final String label = I18nUtils.getMessageResourceString("item_categoryAll", pLocale);
		final SelectItem si = new SelectItem(null, label);
		return si;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Long pCategoryId) {
		this.categoryId = UiUtils.getPositiveLongOrNull(pCategoryId);
	}

    public void category(final ValueChangeEvent pEevent) {
    	final Long categoryId = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setCategoryId(categoryId);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }    
    
    public List<SelectItem> getOrderBySelectItems() {
		if (orderBySelectItems == null) {
			final List<SelectItem> result = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			
			result.add(new SelectItem(new Long(1), I18nUtils.getMessageResourceString("item_orderByTitleAsc", locale)));
			result.add(new SelectItem(new Long(2), getOrderBySelectItemsByCreationDateLabel()));
			
			orderBySelectItems = result;
		}		
		return orderBySelectItems;	
	}
    
    protected String getOrderBySelectItemsByCreationDateLabel() {
    	final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    	return I18nUtils.getMessageResourceString("item_orderByCreationDateDesc", locale);
    }
	
	public Long getOrderBy() {
		return orderBy;
	}
	
	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public void orderBy(final ValueChangeEvent pEevent) {
    	final Long orderBy = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setOrderBy(orderBy);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
	protected String getOrderByField() {
		final long orderByLong = getOrderBy().longValue();
		if (orderByLong == 1) {
			return "title";
		}
		else if (orderByLong == 2) {
			return "creationDate";
		}
		throw new RuntimeException("Unsupported orderBy value: " + orderByLong);
	}
	
	protected Boolean getOrderByAscending() {
		final long orderByLong = getOrderBy().longValue();
		if (orderByLong == 1) {
			return Boolean.TRUE;
		}
		else if (orderByLong == 2) {
			return Boolean.FALSE;
		}
		throw new RuntimeException("Unsupported orderBy value: " + orderByLong);
	}

	public String clearCategory() {
		setCategoryId(null);
		return "clearCategory";
	}

	@Override
	public boolean isFilteredList() {
		boolean tempResult = getCategoryId() != null; 
		return tempResult || super.isFilteredList();
	}

	public String getCategoryLabel() {
		final CategoryEnabled ce = (CategoryEnabled)getTable().getRowData();
		if (ce != null && ce.getCategory() != null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString(ce.getCategory().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}
	
	public abstract String getCreationDateLabel();

	public abstract boolean isOwner();
	
	public abstract String getOwnerHref();
	
	public abstract String getOwnerLabel();
}
