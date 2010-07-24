package com.pferrot.lendity.item.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.ItemCategory;
import com.pferrot.lendity.utils.UiUtils;

public class InternalItemsImportController  {
	
	private final static Log log = LogFactory.getLog(InternalItemsImportController.class);

	private ItemService itemService;
	
	private UploadedFile uploadFile;
	private List<SelectItem> categoriesSelectItems;
	private Long categoryId;
	private Boolean visible = Boolean.TRUE;
	
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

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
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
			final InternalItem internalItem = new InternalItem();
			internalItem.setTitle(itemTitle);
			internalItem.setOwner(getItemService().getCurrentPerson());
			internalItem.setVisible(getVisible());
			getItemService().createItemWithCategory(internalItem, getCategoryId());
		}
	}
	
	public String getVisibilityLabel() {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (visible != null && visible.booleanValue()) {
			return I18nUtils.getMessageResourceString("item_visible", locale);
		}
		else {
			return I18nUtils.getMessageResourceString("item_visibleNo", locale);
		}
	}
	
	public String getCategoryLabel() {
		final ItemCategory category = (ItemCategory)getItemService().getListValue(getCategoryId());
		if (category == null || category.getLabelCode() == null) {
			return null;
		}
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return I18nUtils.getMessageResourceString(category.getLabelCode(), locale);
	}
}