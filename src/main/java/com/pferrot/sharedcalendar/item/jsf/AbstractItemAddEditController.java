package com.pferrot.sharedcalendar.item.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.utils.JsfUtils;
import com.pferrot.sharedcalendar.utils.UiUtils;

public abstract class AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractItemAddEditController.class);
	
	private ItemService itemService;
	
	private List<SelectItem> categoriesSelectItems;
	private List<Long> categoriesId;
	private String title;
	private String description;
	
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
		}		
		return categoriesSelectItems;	
	}
	
	public List<Long> getCategoriesId() {
		return categoriesId;
	}

	public void setCategoriesId(List<Long> categoriesId) {
		this.categoriesId = categoriesId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public abstract Long processItem();
	
	public String submit() {
		Long itemId = processItem();
		
		JsfUtils.redirect(PagesURL.ITEM_OVERVIEW, PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID, itemId.toString());
	
		// As a redirect is used, this is actually useless.
		return null;
	}	
}
