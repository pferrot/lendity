package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.utils.JsfUtils;

@ViewController(viewIds={"/auth/item/itemEdit.jspx"})
public class ItemEditController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(ItemEditController.class);
	
	private Item item;

	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.ITEM_EDIT_PARAM_ITEM_ID);
			if (itemIdString != null) {
				setItem(getItemService().findItem(Long.parseLong(itemIdString)));
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

	public Item getItem() {
		return item;
	}
	
	private void setItem(final Item pItem) {
		item = pItem;

		// Initialize the model to be edited.
		setTitle(pItem.getTitle());
		setDescription(pItem.getDescription());
		setCategoriesId(getItemService().getIdsFromItemCategories(pItem.getCategories()));
	}	

	public Long updateItem() {		
		getItem().setTitle(getTitle());
		getItem().setDescription(getDescription());
		getItemService().updateItemWithCategories(getItem(), getCategoriesId());

		return getItem().getId();
	}

	public String getItemOverviewHref() {		
		return ItemUtils.getItemOverviewPageUrl(item.getId().toString());
	}	

	@Override
	public Long processItem() {
		return updateItem();
	}	
}
