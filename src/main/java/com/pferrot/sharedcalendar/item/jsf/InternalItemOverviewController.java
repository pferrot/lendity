package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.viewController.annotations.InitView;
import org.apache.myfaces.orchestra.viewController.annotations.ViewController;

import com.pferrot.sharedcalendar.PagesURL;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.model.InternalItem;
import com.pferrot.sharedcalendar.utils.JsfUtils;

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
		try {
			final String itemIdString = JsfUtils.getRequestParameter(PagesURL.INTERNAL_ITEM_OVERVIEW_PARAM_ITEM_ID);
			InternalItem item = null;
			if (itemIdString != null) {
				itemId = Long.parseLong(itemIdString);
				item = itemService.findInternalItem(itemId);
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

	public InternalItem getItem() {
		return item;
	}

	public void setItem(InternalItem item) {
		this.item = item;
	}

	public String getItemEditHref() {		
		return ItemUtils.getInternalItemEditPageUrl(item.getId().toString());
	}		
}
