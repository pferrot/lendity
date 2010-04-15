package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.ExternalItem;
import com.pferrot.sharedcalendar.model.InternalItem;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.utils.UiUtils;

public abstract class AbstractItemsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractItemsListController.class);
	
	private ItemService itemService;

	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
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

	private SelectItem getAllCategoriesSelectItem(final Locale locale) {
		final String label = I18nUtils.getMessageResourceString("item_categoryAll", locale);
		final SelectItem si = new SelectItem(null, label);
		return si;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public int getNbEntriesPerPage() {
		return ItemConsts.NB_ITEMS_PER_PAGE;
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
