package com.pferrot.lendity.item.jsf;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.lendity.PagesURL;
import com.pferrot.lendity.item.ItemService;
import com.pferrot.lendity.utils.JsfUtils;

public class DeleteItemTooltipController implements Serializable {
	
	private final static Log log = LogFactory.getLog(DeleteItemTooltipController.class);
	
	private ItemService itemService;
	
	private Long itemId;
	
	// 1 == my items page
	// 2 == item overview page
	private Long redirectId;

	public void setItemService(final ItemService pItemService) {
		this.itemService = pItemService;
	}
	
	public ItemService getItemService() {
		return itemService;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}	

	public Long getRedirectId() {
		return redirectId;
	}

	public void setRedirectId(Long redirectId) {
		this.redirectId = redirectId;
	}

	public String submit() {
		deleteItem();
		
		JsfUtils.redirect(PagesURL.MY_ITEMS_LIST);
	
		// As a redirect is used, this is actually useless.
		return null;
	}

	private void deleteItem() {
		getItemService().deleteItem(getItemId());		
	}	
}
