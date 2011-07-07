package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.model.ItemVisibility;
import com.pferrot.lendity.model.ListValue;
import com.pferrot.lendity.utils.UiUtils;

public class ItemsImportController  {
	
	private final static Log log = LogFactory.getLog(ItemsImportController.class);

	private ItemService itemService;
	
	private UploadedFile uploadFile;
	
	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	
	private List<SelectItem> visibilitySelectItems;
	private Long visibilityId;
	
	
	//private Set<String> itemsToImport = null;
	
	private Set<String> validItemsToImport = null;
	private Set<String> alreadyExistItemsToImport = null;
	private Set<String> titleTooLongItemsToImport = null;
	
	public ItemService getItemService() {
		return itemService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public UploadedFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(UploadedFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public List<SelectItem> getCategoriesSelectItems() {
		if (categoriesSelectItems == null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			categoriesSelectItems = UiUtils.getSelectItemsForListValueWithItemFirst(itemService.getCategories(), locale, ItemCategory.OTHER_LABEL_CODE);
			categoriesSelectItems.add(0, UiUtils.getPleaseSelectSelectItem(locale));
		}		
		return categoriesSelectItems;	
	}	

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final Long pCategoryId) {
		this.categoryId = UiUtils.getPositiveLongOrNull(pCategoryId);
	}
	
	public List<SelectItem> getVisibilitySelectItems() {
		if (visibilitySelectItems == null) {
			final Locale locale = I18nUtils.getDefaultLocale();
			visibilitySelectItems = UiUtils.getSelectItemsForOrderedListValue(itemService.getVisibilities(), locale);
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

	public Set<String> getValidItemsToImport() {
		return validItemsToImport;
	}
	
	public List<String> getValidItemsToImportAsList() {
		return new ArrayList<String>(getValidItemsToImport());
	}

	public void setValidItemsToImport(Set<String> validItemsToImport) {
		this.validItemsToImport = validItemsToImport;
	}

	public Set<String> getAlreadyExistItemsToImport() {
		return alreadyExistItemsToImport;
	}
	
	public List<String> getAlreadyExistItemsToImportAsList() {
		return new ArrayList<String>(getAlreadyExistItemsToImport());
	}
	

	public void setAlreadyExistItemsToImport(Set<String> alreadyExistItemsToImport) {
		this.alreadyExistItemsToImport = alreadyExistItemsToImport;
	}

	public Set<String> getTitleTooLongItemsToImport() {
		return titleTooLongItemsToImport;
	}
	
	public List<String> getTitleTooLongItemsToImportAsList() {
		return new ArrayList<String>(getTitleTooLongItemsToImport());
	}

	public void setTitleTooLongItemsToImport(Set<String> titleTooLongItemsToImport) {
		this.titleTooLongItemsToImport = titleTooLongItemsToImport;
	}

	public int getNbValidItemsToImport() {
		return getValidItemsToImport().size();
	}
	
	public int getNbAlreadyExistItemsToImport() {
		return getAlreadyExistItemsToImport().size();
	}
	
	public int getNbTitleTooLongItemsToImport() {
		return getTitleTooLongItemsToImport().size();
	}
	
	public void createItems() {
		for (String itemTitle: validItemsToImport) {
			final Item item = new Item();
			item.setTitle(itemTitle);
			item.setOwner(getItemService().getCurrentPerson());
			item.setToGiveForFree(Boolean.FALSE);
			getItemService().createItem(item, getCategoryId(), getVisibilityId());
		}
	}
	
	public String getVisibilityLabel() {
		final ItemVisibility visibility = (ItemVisibility)getItemService().getListValue(getVisibilityId());
		if (visibility == null || visibility.getLabelCode() == null) {
			return null;
		}
		final Locale locale = I18nUtils.getDefaultLocale();
		return I18nUtils.getMessageResourceString(visibility.getLabelCode(), locale);
	}
	
	public String getCategoryLabel() {
		final ItemCategory category = (ItemCategory)getItemService().getListValue(getCategoryId());
		if (category == null || category.getLabelCode() == null) {
			return null;
		}
		final Locale locale = I18nUtils.getDefaultLocale();
		return I18nUtils.getMessageResourceString(category.getLabelCode(), locale);
	}

	public void validateVisibility(FacesContext context, UIComponent toValidate, Object value) {
		String message = "";
		Long visibilityId = (Long) value;
		
		final UIComponent categoryComponent = toValidate.findComponent("category");
		final EditableValueHolder categoryEditableValueHolder = (EditableValueHolder)categoryComponent;
		final Long categoryId = (Long)categoryEditableValueHolder.getValue();
		
		if (categoryId != null &&
			visibilityId != null &&
			!getItemService().isVisibilityAllowed(visibilityId, categoryId)) {
			((UIInput)toValidate).setValid(false);
			final Locale locale = I18nUtils.getDefaultLocale();
			final ListValue category = getItemService().getListValueDao().findListValue(categoryId);
			final String categoryLabel = I18nUtils.getMessageResourceString(category.getLabelCode(), locale);
			message = I18nUtils.getMessageResourceString("validation_visibilityNotAllowedIntellectualProperty", new Object[]{categoryLabel}, locale);
			context.addMessage(toValidate.getClientId(context), new FacesMessage(message));			
		}
	}
}