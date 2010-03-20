package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.utils.JsfUtils;

@ViewController(viewIds={"/public/item/itemOverview.jspx"})
public class ItemOverviewController
{
	private final static Log log = LogFactory.getLog(ItemOverviewController.class);
	
	private ItemService itemService;
	private Long itemId;
	private Item item;
	
	@InitView
	public void initView() {
		// Read the item ID from the request parameter and load the correct item.
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.ITEM_OVERVIEW_PARAM_ITEM_ID);
			Item item = null;
			if (itemIdString != null) {
				itemId = Long.parseLong(itemIdString);
				item = itemService.findItem(itemId);
				setItem(item);
			}
			// Item not found or not item ID specified.
			if (item == null) {
				JsfUtils.redirect(PagesURL.ITEMS_LIST);
			}
		}
		catch (Exception e) {
			//TODO display standard error page instead.
			JsfUtils.redirect(PagesURL.ITEMS_LIST);
		}		
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	public String getItemTitle() {
		return getItem().getTitle();
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getItemEditHref() {		
		return ItemUtils.getItemEditPageUrl(item.getId().toString());
	}		
}
