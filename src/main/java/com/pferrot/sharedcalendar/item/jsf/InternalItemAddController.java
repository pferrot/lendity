package com.pferrot.sharedcalendar.item.jsf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.item.ItemUtils;
import com.pferrot.sharedcalendar.model.InternalItem;

public class InternalItemAddController extends AbstractInternalItemAddEditController {
	
	private final static Log log = LogFactory.getLog(InternalItemAddController.class);

	public Long createItem() {
		InternalItem internalItem = new InternalItem();
		
		internalItem.setTitle(getTitle());
		internalItem.setDescription(getDescription());
		internalItem.setOwner(getItemService().getCurrentPerson());
				
		return getItemService().createItemWithCategory(internalItem, getCategoryId());		
	}

	public String getItemsListHref() {		
		return ItemUtils.getItemsListUrl();
	}
	
	@Override
	public Long processItem() {
		return createItem();
	}
}
