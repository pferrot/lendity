package com.pferrot.lendity.item.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.i18n.I18nUtils;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.lendrequest.LendRequestService;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/internalItemOverview.jspx"})
public class InternalItemOverviewController extends AbstractItemOverviewController
{
	private final static Log log = LogFactory.getLog(InternalItemOverviewController.class);
	
	private LendRequestService lendRequestService;
	
	
	public LendRequestService getLendRequestService() {
		return lendRequestService;
	}

	public void setLendRequestService(LendRequestService lendRequestService) {
		this.lendRequestService = lendRequestService;
	}

	@InitView
	public void initView() {		
		final String itemIdString = JsfUtils.getRequestParameter(PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID);
		InternalItem item = null;
		if (itemIdString != null) {
			setItemId(Long.parseLong(itemIdString));
			item = getItemService().findInternalItem(getItemId());
			// Access control check.
			if (!getItemService().isCurrentUserAuthorizedToView(item)) {
				JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
				if (log.isWarnEnabled()) {
					log.warn("Access denied (internal item view): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), item = " + getItemId());
				}
				return;
			}
			setItem(item);
		}	
	}
	
	public String getVisibleLabel() {
		final InternalItem internalItem = (InternalItem)getItem();
		if (Boolean.TRUE.equals(internalItem.getVisible())) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("item_visibleYes", locale);
		}
		else if (Boolean.FALSE.equals(internalItem.getVisible())) {
			final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			return I18nUtils.getMessageResourceString("item_visibleNo", locale);
		}
		return "";
	}
	
	public boolean isLendAvailable() {
		return isEditAvailable() && !getItem().isBorrowed();
	}
	
	public boolean isLendBackAvailable() {
		return isEditAvailable() && getItem().isBorrowed();
	}
	
	public String getOwnerHref() {
		return PersonUtils.getPersonOverviewPageUrl(((InternalItem)getItem()).getOwner().getId().toString());
	}
	
	@Override
	public String getItemEditHref() {		
		return ItemUtils.getInternalItemEditPageUrl(((InternalItem)getItem()).getId().toString());
	}
	
	public String getItemEditPictureHref() {		
		return ItemUtils.getInternalItemEditPicturePageUrl(((InternalItem)getItem()).getId().toString());
	}
	
	public boolean isRequestLendAvailable() {		
		return getLendRequestService().isLendRequestAllowedFromCurrentUser((InternalItem)getItem());
	}
	
	public String getImageButtonLabel() {
		final Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		if (getItem().getImage1() == null) {
			return I18nUtils.getMessageResourceString("image_addImage", locale);
		}
		else {
			return I18nUtils.getMessageResourceString("image_changeImage", locale);
		}
	}
}
