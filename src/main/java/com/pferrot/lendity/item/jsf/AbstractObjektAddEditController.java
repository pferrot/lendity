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
import com.pferrot.lendity.item.ObjektService;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractObjektAddEditController {
	
	private final static Log log = LogFactory.getLog(AbstractObjektAddEditController.class);
	
	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	private String title;
	private String description;
	private List<SelectItem> visibilitySelectItems;
	private Long visibilityId;

	protected abstract ObjektService getObjektService();
	
	public List<SelectItem> getCategoriesSelectItems() {
		if (categoriesSelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			categoriesSelectItems = UiUtils.getSelectItemsForListValue(getObjektService().getCategories(), locale);
			categoriesSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return categoriesSelectItems;	
	}	

	public Long getCategoryId() {
		return categoryId;
	}

	public List<SelectItem> getVisibilitySelectItems() {
		if (visibilitySelectItems == null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			visibilitySelectItems = UiUtils.getSelectItemsForOrderedListValue(getObjektService().getVisibilities(), locale);
			visibilitySelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return visibilitySelectItems;	
	}

	public Long getVisibilityId() {
		return visibilityId;
	}

	public void setVisibilityId(final Long pVisibilityId) {
		this.visibilityId = UiUtils.getPositiveLongOrNull(pVisibilityId);
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = UiUtils.getPositiveLongOrNull(categoryId);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = StringUtils.getNullIfEmpty(title);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = StringUtils.getNullIfEmpty(description);
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
