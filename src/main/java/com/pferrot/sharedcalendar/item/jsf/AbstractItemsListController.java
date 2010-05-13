package com.pferrot.sharedcalendar.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.ExternalItem;
import com.pferrot.sharedcalendar.model.InternalItem;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.utils.UiUtils;

public abstract class AbstractItemsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractItemsListController.class);
	
	private ItemService itemService;

	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	
	private List<SelectItem> borrowStatusSelectItems;
	// Cannot use Boolean because selecting the SelectItem with value null actually
	// sets the value False...
	// 1 = True = borrowed by someone
	// 2 = False = not borrowed
	// null = all items
	private Long borrowStatus;

	private List<SelectItem> visibleStatusSelectItems;
	// Cannot use Boolean because selecting the SelectItem with value null actually
	// sets the value False...
	// 1 = True = visible by connections
	// 2 = False = not visible by connections.
	// null = all items
	private Long visibleStatus;
	
	
	public AbstractItemsListController() {
		super();
		setRowsPerPage(ItemConsts.NB_ITEMS_PER_PAGE);
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

	public List<SelectItem> getBorrowStatusSelectItems() {
		if (getBorrowStatusSelectItemsInternal() == null) {
			final List result = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			
			result.add(new SelectItem(UiUtils.getLongFromBoolean(null), I18nUtils.getMessageResourceString("item_borrowStatusAll", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.TRUE), I18nUtils.getMessageResourceString("item_borrowStatusBorrowed", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.FALSE), I18nUtils.getMessageResourceString("item_borrowStatusNotBorrowed", locale)));
			
			setBorrowStatusSelectItemsInternal(result);
		}		
		return getBorrowStatusSelectItemsInternal();	
	}
	
	protected List<SelectItem> getBorrowStatusSelectItemsInternal() {
		return borrowStatusSelectItems;
	}

	protected void setBorrowStatusSelectItemsInternal(final List<SelectItem> pBorrowStatusSelectItems) {
		this.borrowStatusSelectItems = pBorrowStatusSelectItems;
	}

	public Long getBorrowStatus() {
		return borrowStatus;
	}
	
	public Boolean getBorrowStatusBoolean() {
		return UiUtils.getBooleanFromLong(borrowStatus);
	}

	public void setBorrowStatus(final Long pBorrowStatus) {
		this.borrowStatus = UiUtils.getPositiveLongOrNull(pBorrowStatus);
	}

    public void borrowStatus(final ValueChangeEvent event) {
    	final Long borrowStatus = (Long) ((HtmlSelectOneMenu) event.getComponent()).getValue();
        setBorrowStatus(borrowStatus);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }    

	public List<SelectItem> getVisibleStatusSelectItems() {
		if (visibleStatusSelectItems == null) {
			final List result = new ArrayList<SelectItem>();
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			
			result.add(new SelectItem(UiUtils.getLongFromBoolean(null), I18nUtils.getMessageResourceString("item_visibleStatusAll", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.TRUE), I18nUtils.getMessageResourceString("item_visibleStatusVisible", locale)));
			result.add(new SelectItem(UiUtils.getLongFromBoolean(Boolean.FALSE), I18nUtils.getMessageResourceString("item_visibleStatusNotVisible", locale)));
			
			visibleStatusSelectItems = result;
		}		
		return visibleStatusSelectItems;	
	}

	public Long getVisibleStatus() {
		return visibleStatus;
	}

	public Boolean getVisibleStatusBoolean() {
		return UiUtils.getBooleanFromLong(visibleStatus);
	}

	public void setVisibleStatus(final Long pVisibleStatus) {
		this.visibleStatus = UiUtils.getPositiveLongOrNull(pVisibleStatus);
	}

    public void visibleStatus(final ValueChangeEvent pEevent) {
    	final Long borrowStatus = (Long) ((HtmlSelectOneMenu) pEevent.getComponent()).getValue();
        setVisibleStatus(borrowStatus);
        // loadDataList() is not called by getList() when the page is submitted with the onchange
        // event on the h:selectOneMenu. Not sure why!?
        reloadList();
    }
	
	public String clearBorrowStatus() {
		setBorrowStatus(null);
		return "clearBorrowStatus";
	}

	public String clearVisibleStatus() {
		setVisibleStatus(null);
		return "clearVisibleStatus";
	}

	public String clearCategory() {
		setCategoryId(null);
		return "clearCategory";
	}

	@Override
	public boolean isFilteredList() {
		boolean tempResult = getCategoryId() != null || getBorrowStatusBoolean() != null || getVisibleStatusBoolean() != null; 
		return tempResult || super.isFilteredList();
	}

	public String getCategoryLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item != null && item.getCategory() != null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString(item.getCategory().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}

	public String getVisibleLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			InternalItem internalItem = (InternalItem) item;
			if (Boolean.TRUE.equals(internalItem.getVisible())) {
				final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				return I18nUtils.getMessageResourceString("item_visibleYes", locale);
			}
			else if (Boolean.FALSE.equals(internalItem.getVisible())) {
				final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				return I18nUtils.getMessageResourceString("item_visibleNo", locale);
			}
		}
		return "";
	}

	public String getAvailableLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			InternalItem internalItem = (InternalItem) item;
			if (internalItem.isAvailable()) {
				final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				return I18nUtils.getMessageResourceString("item_availableYes", locale);
			}
			else {
				final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				return I18nUtils.getMessageResourceString("item_availableNo", locale);
			}
		}
		return "";
	}


	public String getDescription() {
		final Item item = (Item)getTable().getRowData();
		if (item != null && item.getDescription() != null) {
			String description = item.getDescription();
			if (description.length() > ItemConsts.NB_CHARACTERS_DESCRIPTION_IN_LISTS) {
				description = description.substring(0, ItemConsts.NB_CHARACTERS_DESCRIPTION_IN_LISTS - 3);
				description = description + "...";
			}
			return description;
		}
		else {
			return "";
		}
	}
	
	public boolean isEditAvailable() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().isCurrentUserAuthorizedToEdit(item);
	}

	public boolean isLendAvailable() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().isCurrentUserAuthorizedToEdit(item) &&
			!item.isBorrowed();
		
	}

	public boolean isLendBackAvailable() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().isCurrentUserAuthorizedToEdit(item) &&
			item.isBorrowed();
	}

	public boolean isAddAvailable() {
		return getItemService().isCurrentUserAuthorizedToAdd();
	}

	public boolean isDeleteAvailable() {
		final Item item = (Item)getTable().getRowData();
		return getItemService().isCurrentUserAuthorizedToDelete(item);
	}
	
	public boolean isOwner() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			return getItemService().isCurrentUserOwner((InternalItem)item);
		}
		return false;
	}

	public boolean isBorrowed() {
		final Item item = (Item)getTable().getRowData();
		return item != null && item.isBorrowed();
	}

	public String getBorrowerLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item != null && item.isBorrowed()) {
			if (item.getBorrower() != null) {
				return item.getBorrower().getDisplayName();
			}
			else if (! StringUtils.isNullOrEmpty(item.getBorrowerName())) {
				return item.getBorrowerName();
			}
		}
		return "";
	}

	public String getBorrowDateLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item != null && item.isBorrowed()) {
			return UiUtils.getDateAsString(item.getBorrowDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		return "";
	}

	public String getOwnerLabel() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			final InternalItem internalItem = (InternalItem) item;
			if (internalItem.getOwner() != null && internalItem.getOwner().getDisplayName() != null) {
				return internalItem.getOwner().getDisplayName();
			}
		}
		else if (item instanceof ExternalItem) {
			final ExternalItem externalItem = (ExternalItem) item;
			if (externalItem.getOwnerName() != null) {
				return externalItem.getOwnerName();
			}
		}
		return "";
	}

	public boolean isAvailable() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			return ((InternalItem) item).isAvailable();
		}
		else {
			return false;
		}
	}

	public String getBorrowerHref() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			final InternalItem internalItem = (InternalItem) item;
			if (internalItem.getBorrower() != null) {
				return PersonUtils.getPersonOverviewPageUrl(internalItem.getBorrower().getId().toString());
			}
		}
		return null;		
	}
	
	public boolean isBorrowerHrefAvailable() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			final InternalItem internalItem = (InternalItem) item;
			return internalItem.getBorrower() != null;
		}
		return false;
	}
	
	public String getItemOverviewHref() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			return ItemUtils.getInternalItemOverviewPageUrl(((InternalItem)item).getId().toString());
		}
		else {
			return ItemUtils.getExternalItemOverviewPageUrl(((ExternalItem)item).getId().toString());
		}		
	}
	
	public String getInternalItemAddHref() {
		return ItemUtils.getInternalItemAddPageUrl();
	}

	public String getItemEditHref() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			return ItemUtils.getInternalItemEditPageUrl(((InternalItem)item).getId().toString());
		}
		else {
			return ItemUtils.getInternalItemEditPageUrl(((ExternalItem)item).getId().toString());
		}
	}
}
