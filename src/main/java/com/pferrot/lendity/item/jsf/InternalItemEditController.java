package com.pferrot.lendity.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemUtils;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.person.PersonUtils;
import com.pferrot.lendity.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/internalItemEdit.jspx"})
public class InternalItemEditController extends AbstractInternalItemAddEditController {
	
	private final static Log log = LogFactory.getLog(InternalItemEditController.class);
	
	private InternalItem item;

	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.INTERNAL_ITEM_EDIT_PARAM_ITEM_ID);
			if (itemIdString != null) {
				setItem(getItemService().findInternalItem(Long.parseLong(itemIdString)));
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

	public InternalItem getItem() {
		return item;
	}
	
	private void setItem(final InternalItem pItem) {
		item = pItem;

		// Initialize the model to be edited.
		setTitle(pItem.getTitle());
		setDescription(pItem.getDescription());
		setCategoryId(pItem.getCategory().getId());
		setVisible(pItem.getVisible());
	}	

	public Long updateItem() {		
		getItem().setTitle(getTitle());
		getItem().setDescription(getDescription());
		getItem().setVisible(getVisible());
		getItemService().updateItemWithCategory(getItem(), getCategoryId());

		return getItem().getId();
	}

	public String getItemOverviewHref() {		
		return ItemUtils.getInternalItemOverviewPageUrl(item.getId().toString());
	}	

	@Override
	public Long processItem() {
		return updateItem();
	}	
}
