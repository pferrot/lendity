package com.pferrot.lendity.item.jsf;

import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemConsts;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractObjectAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractObjectAddEditController.class);
	
	private ItemService itemService;
	
	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	private String title;
	private String description;

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public List<SelectItem> getCategoriesSelectItems() {
		if (categoriesSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			categoriesSelectItems = UiUtils.getSelectItemsForListValue(itemService.getCategories(), locale);
			categoriesSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return categoriesSelectItems;	
	}	

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = UiUtils.getPositiveLongOrNull(categoryId);
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
	
	public boolean isDescriptionAvailable() {
		return !StringUtils.isNullOrEmpty(description);
	}

	public void validateDescriptionSize(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		String description = (String) value;
		if (description != null && description.length() > ItemConsts.MAX_DESCRIPTION_SIZE) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			message = I18nUtils.getMessageResourceString("validation_maxSizeExceeded", new Object[]{String.valueOf(ItemConsts.MAX_DESCRIPTION_SIZE)}, locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));
		}
	}
}
