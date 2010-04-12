package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemConsts;
import com.pferrot.sharedcalendar.item.ItemService;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.jsf.list.AbstractListController;
import com.pferrot.sharedcalendar.model.ExternalItem;
import com.pferrot.sharedcalendar.model.InternalItem;
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
		if (item instanceof InternalItem) {
		return ((InternalItem) item).isAvailable();
		}
		else {
			return false;
		}
	}
	
	public String getItemOverviewHref() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			return ItemUtils.getInternalItemOverviewPageUrl(((InternalItem)item).getId().toString());
		}
		else {
			return ItemUtils.getExternalItemOverviewPageUrl(((ExternalItem)item).getId().toString());
		}		
	}
	
	public String getInternalItemAddHref() {
		return ItemUtils.getInternalItemAddPageUrl();
	}

	public String getItemEditHref() {
		final Item item = (Item)getTable().getRowData();
		if (item instanceof InternalItem) {
			return ItemUtils.getInternalItemEditPageUrl(((InternalItem)item).getId().toString());
		}
		else {
			return ItemUtils.getInternalItemEditPageUrl(((ExternalItem)item).getId().toString());
		}
	}
}
