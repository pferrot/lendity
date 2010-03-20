package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.Item;

public abstract class AbstractItemsListController extends AbstractListController {
	private final static Log log = LogFactory.getLog(AbstractItemsListController.class);
	
	private ItemService itemService;
	
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}

	@Override
	public int getNbEntriesPerPage() {
		return ItemConsts.NB_ITEMS_PER_PAGE;
	}

	public boolean isAvailable() {
		final Item item = (Item)getTable().getRowData();
		return item.isAvailable();
	}
	
	public String getItemOverviewHref() {
		final Item item = (Item)getTable().getRowData();		
		return ItemUtils.getItemOverviewPageUrl(item.getId().toString());
	}	
}
