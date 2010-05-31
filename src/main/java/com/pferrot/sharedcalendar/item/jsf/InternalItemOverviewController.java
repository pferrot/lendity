package com.pferrot.sharedcalendar.item.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.core.StringUtils;
import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.i18n.I18nUtils;
import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.model.InternalItem;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.utils.HtmlUtils;
import com.pferrot.sharedcalendar.utils.JsfUtils;
import com.pferrot.sharedcalendar.utils.UiUtils;

@ViewController(viewIds={"/auth/item/internalItemOverview.jspx"})
public class InternalItemOverviewController
{
	private final static Log log = LogFactory.getLog(InternalItemOverviewController.class);
	
	private ItemService itemService;
	private Long itemId;
	private InternalItem item;
	
	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
//		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID);
			InternalItem item = null;
			if (itemIdString != null) {
				itemId = Long.parseLong(itemIdString);
				item = itemService.findInternalItem(itemId);
				setItem(item);
			}
			// Item not found or not item ID specified.
//			if (item == null) {
//				JsfUtils.redirect(PagesURL.MY_ITEMS_LIST);
//			}
//		}
//		catch (Exception e) {
			//TODO display standard error page instead.
//			JsfUtils.redirect(PagesURL.MY_ITEMS_LIST);
//		}		
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	public String getTitle() {
		return getItem().getTitle();
	}

	public InternalItem getItem() {
		return item;
	}

	public void setItem(InternalItem item) {
		this.item = item;
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

	public String getVisibleLabel() {
		if (Boolean.TRUE.equals(item.getVisible())) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("item_visibleYes", locale);
		}
		else if (Boolean.FALSE.equals(item.getVisible())) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("item_visibleNo", locale);
		}
		return "";
	}

	public String getDescription() {
		final String itemDescription = item.getDescription();
		if (itemDescription != null) {
			return HtmlUtils.escapeHtmlAndReplaceCrAndWhiteSpaces(itemDescription);
		}
		return "";
	}
	
	public boolean isEditAvailable() {
		return itemService.isCurrentUserAuthorizedToEdit(item);
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

	public String getBorrowerHref() {
		if (item.getBorrower() != null) {
			return PersonUtils.getPersonOverviewPageUrl(item.getBorrower().getId().toString());
		}
		return null;		
	}
	
	public boolean isBorrowerHrefAvailable() {
		return item.getBorrower() != null;
	}

	public String getItemEditHref() {		
		return ItemUtils.getInternalItemEditPageUrl(item.getId().toString());
	}
}
