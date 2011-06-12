package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.geolocation.GeoLocationUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemConsts;
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.jsf.list.AbstractListController;
import com.pferrot.lendity.model.CategoryEnabled;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.Objekt;
import com.pferrot.lendity.model.Ownable;
import com.pferrot.lendity.model.VisibilityEnabled;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractObjektsListController extends AbstractListController {
	
	private final static Log log = LogFactory.getLog(AbstractObjektsListController.class);
	
	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	
	private List<SelectItem> orderBySelectItems;
	// null = by title ascending
	// 1 = by distance
	// 2 = by creationDate descending
	private Long orderBy;
	
	private List<SelectItem> visibilitySelectItems;
	private Long visibilityId;
	
	private List<SelectItem> maxDistanceSelectItems;
	private Long maxDistance;
	
	// null == all (default)
	// 1 == connections items only
	private Long ownerType;
	
	public AbstractObjektsListController() {
		super();
		setRowsPerPage(ItemConsts.NB_ITEMS_PER_PAGE);
	}
	
	@Override
	protected void resetFilters() {
		super.resetFilters();
		setVisibilityId(null);
		setCategoryId(null);
		setMaxDistance(null);
		setOwnerType(null);
	}

	public List<SelectItem> getVisibilitySelectItems() {
		if (visibilitySelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			visibilitySelectItems = UiUtils.getSelectItemsForOrderedListValue(getObjektService().getVisibilities(), locale);
			// Add all visibilities first.
			visibilitySelectItems.add(0, getAllVisibilitiesSelectItem(locale));
		}		
		return visibilitySelectItems;	
	}
	
	private SelectItem getAllVisibilitiesSelectItem(final Locale pLocale) {
		final String label = I18nUtils.getMessageResourceString("item_visibilityAll", pLocale);
		final SelectItem si = new SelectItem(null, label);
		return si;
	}

	public Long getVisibilityId() {
		return visibilityId;
	}

	public void setVisibilityId(final Long pVisibilityId) {
		this.visibilityId = UiUtils.getPositiveLongOrNull(pVisibilityId);
	}

	public void visibility(final ValueChangeEvent pEevent) {
    	final Long visibility = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setVisibilityId(visibility);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }

	public String getVisibilityLabel() {
		final VisibilityEnabled visibilityEnabled = (VisibilityEnabled)getTable().getRowData();
		if (visibilityEnabled != null && visibilityEnabled.getVisibility() != null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString(visibilityEnabled.getVisibility().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}

	public List<SelectItem> getOwnerTypeSelectItems() {
		final List<SelectItem> result = new ArrayList<SelectItem>();
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		
		result.add(new SelectItem(null, I18nUtils.getMessageResourceString("item_ownerTypeAll", locale)));
		result.add(new SelectItem(Long.valueOf(ItemConsts.OWNER_TYPE_CONNECTIONS), I18nUtils.getMessageResourceString("item_ownerTypeConnections", locale)));
		
		return result;
	}

	public Long getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(final Long pOwnerType) {
		this.ownerType = UiUtils.getPositiveLongOrNull(pOwnerType);
	}

    public void ownerType(final ValueChangeEvent event) {
    	final Long ownerType = (Long) ((HtmlSelectOneMenu) event.getComponent()).getValue();
        setOwnerType(ownerType);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
    
	public String clearOwnerType() {
		setOwnerType(null);
		return "clearOwnerType";
	}

	public Long getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Long maxDistance) {
		this.maxDistance = UiUtils.getPositiveLongOrNull(maxDistance);
	}
	
	public List<SelectItem> getMaxDistanceSelectItems() {
		if (maxDistanceSelectItems == null) {
			maxDistanceSelectItems = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			// Add all categories first.
			maxDistanceSelectItems.add(getNoMaxDistanceSelectItem(locale));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(1), "1 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(5), "5 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(10), "10 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(20), "20 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(50), "50 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(100), "100 km"));
			maxDistanceSelectItems.add(new SelectItem(Long.valueOf(500), "500 km"));
		}		
		return maxDistanceSelectItems;	
	}

	private SelectItem getNoMaxDistanceSelectItem(final Locale pLocale) {
		final String label = I18nUtils.getMessageResourceString("geolocation_noMaxDistance", pLocale);
		final SelectItem si = new SelectItem(null, label);
		return si;
	}
	
	public void maxDistance(final ValueChangeEvent pEevent) {
    	final Long maxDistance = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
    	setMaxDistance(maxDistance);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
	public String clearMaxDistance() {
		setMaxDistance(null);
		return "clearMaxDistance";
	}
	
	public String clearVisibility() {
		setVisibilityId(null);
		return "clearVisibility";
	}

	public List<SelectItem> getCategoriesSelectItems() {
		if (categoriesSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			categoriesSelectItems = UiUtils.getSelectItemsForListValueWithItemFirst(getObjektService().getCategories(), locale, ItemCategory.OTHER_LABEL_CODE);
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
			
			result.add(new SelectItem(null, I18nUtils.getMessageResourceString("item_orderByTitleAsc", locale)));
			if (PersonUtils.isCurrentPersonIsAddressDefined()) {
				result.add(new SelectItem(new Long(1), I18nUtils.getMessageResourceString("geolocation_orderByDistanceAsc", locale)));
			}
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
		this.orderBy = UiUtils.getPositiveLongOrNull(orderBy);
	}

	public void orderBy(final ValueChangeEvent pEevent) {
    	final Long orderBy = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setOrderBy(orderBy);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
	protected String getOrderByField() {
		final Long orderByLong = getOrderBy();
		if (orderByLong == null) {
			return "title";
		}
		// Not a field - this is calculated. See in DAO implementation for details.
		else if (orderByLong.longValue() == 1) {
			if (!PersonUtils.isCurrentPersonIsAddressDefined()) {
				setOrderBy(null);
				return "title";
			}
			return "distance";
		}
		else if (orderByLong.longValue() == 2) {
			return "creationDate";
		}
		throw new RuntimeException("Unsupported orderBy value: " + orderByLong);
	}
	
	protected Boolean getOrderByAscending() {
		final Long orderByLong = getOrderBy();
		if (orderByLong == null) {
			return Boolean.TRUE;
		}
		else if (orderByLong.longValue() == 1) {
			if (!PersonUtils.isCurrentPersonIsAddressDefined()) {
				setOrderBy(null);
				return Boolean.TRUE;
			}
			return Boolean.TRUE;
		}
		else if (orderByLong.longValue() == 2) {
			return Boolean.FALSE;
		}
		throw new RuntimeException("Unsupported orderBy value: " + orderByLong);
	}

	public String clearCategory() {
		setCategoryId(null);
		return "clearCategory";
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

	public boolean isOwner() {
		final Ownable ownable = (Ownable)getTable().getRowData();
		return getObjektService().isCurrentUserOwner(ownable);
	}


	public String getOwnerHref() {
		final Ownable ownable = (Ownable)getTable().getRowData();
		return PersonUtils.getPersonOverviewPageUrl(ownable.getOwner().getId().toString());	
	}

	public String getOwnerLabel() {
		final Ownable ownable = (Ownable)getTable().getRowData();
		return ownable.getOwner().getDisplayName();
	}

	public String getCreationDateLabel() {
		final Objekt objekt = (Objekt)getTable().getRowData();
		return UiUtils.getDateAsString(objekt.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
	
	public String getDistanceLabel() {
		final Objekt objekt = (Objekt)getTable().getRowData();
		return GeoLocationUtils.getApproxDistanceKm(PersonUtils.getCurrentPersonAddressHomeLatitude(),
													PersonUtils.getCurrentPersonAddressHomeLongitude(),
													objekt.getOwner().getAddressHomeLatitude(),
													objekt.getOwner().getAddressHomeLongitude());
	}
	
	protected abstract ObjektService getObjektService();
}
