package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.model.ExternalItem;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/externalItemEdit.jspx"})
public class ExternalItemEditController extends AbstractExternalItemAddEditController {
	
	private final static Log log = LogFactory.getLog(ExternalItemEditController.class);
	
	private ExternalItem item;

	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.EXTERNAL_ITEM_EDIT_PARAM_ITEM_ID);
			if (itemIdString != null) {
				setItem(getItemService().findExternalItem(Long.parseLong(itemIdString)));
				// Access control check.
				if (!getItemService().isCurrentUserAuthorizedToEdit(getItem())) {
					JsfUtils.redirect(PagesURL.ERROR_ACCESS_DENIED);
					if (log.isWarnEnabled()) {
						log.warn("Access denied (item edit): user = " + PersonUtils.getCurrentPersonDisplayName() + " (" + PersonUtils.getCurrentPersonId() + "), item = " + itemIdString);
					}
					return;
				}
			}
			// Item not found or no item ID specified.
			if (getItem() == null) {
				JsfUtils.redirect(PagesURL.ITEMS_LIST);
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.ITEMS_LIST);
		}		
	}

	
	public ExternalItem getItem() {
		return item;
	}


	public void setItem(ExternalItem pItem) {
		this.item = pItem;
		
		// Initialize the model to be edited.
		setTitle(pItem.getTitle());
		setDescription(pItem.getDescription());
		setCategoryId(pItem.getCategory().getId());
		setOwnerName(pItem.getOwnerName());
		setBorrowDate(pItem.getBorrowDate());
	}

	public Long updateItem() {		
		getItem().setTitle(getTitle());
		getItem().setDescription(getDescription());
		getItem().setOwnerName(getOwnerName());
		getItem().setBorrowDate(getBorrowDate());
		getItemService().updateItemWithCategory(getItem(), getCategoryId(), null);

		return getItem().getId();
	}

	public String getItemOverviewHref() {		
		return ItemUtils.getExternalItemOverviewPageUrl(item.getId().toString());
	}	

	@Override
	public Long processItem() {
		return updateItem();
	}	
}
