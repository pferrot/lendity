package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.security.SecurityUtils;
import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.model.Item;

public class ItemAddController extends AbstractItemAddEditController {
	
	private final static Log log = LogFactory.getLog(ItemAddController.class);

	public Long createItem() {
		Item item = new Item();
		
		item.setTitle(getTitle());
		item.setDescription(getDescription());
		item.setOwner(SecurityUtils.getCurrentUser());
				
		return getItemService().createItemWithCategories(item, getCategoriesId());		
	}

	public String getItemsListHref() {		
		return ItemUtils.getItemsListUrl();
	}
	
	@Override
	public Long processItem() {
		return createItem();
	}
}
