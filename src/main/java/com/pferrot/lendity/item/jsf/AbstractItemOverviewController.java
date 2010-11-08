package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.document.DocumentService;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.HtmlUtils;
import com.pferrot.lendity.utils.JsfUtils;
import com.pferrot.lendity.utils.UiUtils;

public abstract class AbstractItemOverviewController
{
	private final static Log log = LogFactory.getLog(AbstractItemOverviewController.class);

	private Item item;
	private ItemService itemService;
	private DocumentService documentService;
	private Long itemId;

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public String getTitle() {
		return getItem().getTitle();
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
		if (item.getImage1() != null) {
			documentService.authorizeDownloadOneMinute(JsfUtils.getSession(), item.getImage1().getId());
		}
	}

	public String getImage1URL() {
		if (item.getImage1() == null) {
			return null;
		}
		return JsfUtils.getFullUrl(PagesURL.DOCUMENT_DOWNLOAD, PagesURL.DOCUMENT_DOWNLOAD_PARAM_DOCUMENT_ID, item.getImage1().getId().toString());
	}

	public String getCategoryLabel() {
		if (item != null && item.getCategory() != null) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString(item.getCategory().getLabelCode(), locale);
		}
		else {
			return "";
		}
	}

	public String getDescription() {
		final String itemDescription = item.getDescription();
		if (itemDescription != null) {
			return HtmlUtils.escapeHtmlAndReplaceCr(itemDescription);
		}
		return "";
	}

	public boolean isDescriptionAvailable() {
		final String itemDescription = item.getDescription();
		return !StringUtils.isNullOrEmpty(itemDescription);
	}	
	
	public boolean isEditAvailable() {
		return itemService.isCurrentUserAuthorizedToEdit(item);
	}
	
	public boolean isLendAvailable() {
		return isEditAvailable() && !item.isBorrowed();
	}
	
	public boolean isLendBackAvailable() {
		return isEditAvailable() && item.isBorrowed();
	}

	public boolean isDeleteAvailable() {
		return itemService.isCurrentUserAuthorizedToDelete(item);
	}
	
	public boolean isOwner() {
		return itemService.isCurrentUserOwner(item);
	}

	public boolean isBorrowed() {
		return item.isBorrowed();
	}

	public String getBorrowerLabel() {
		if (item.isBorrowed()) {
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
		if (item.isBorrowed()) {
			return UiUtils.getDateAsString(item.getBorrowDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		return "";
	}
	
	public String getCreationDateLabel() {
		return UiUtils.getDateAsString(item.getCreationDate(), FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}

	public String getBorrowerHref() {
		if (item.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(item.getBorrower().getId().toString());
		}
		return null;		
	}
	
	public boolean isBorrowerHrefAvailable() {
		return item.getBorrower() != null;
	}

	public abstract String getItemEditHref();
}
