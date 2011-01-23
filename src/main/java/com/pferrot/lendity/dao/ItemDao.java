package com.pferrot.lendity.dao;

import java.util.Date;
import java.util.List;

import com.pferrot.lendity.dao.bean.ItemDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ExternalItem;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.Item;

public interface ItemDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Item
	Long createItem(Item item);
	
	InternalItem findInternalItem(Long itemId);
	ExternalItem findExternalItem(Long itemId);
	
	// Returns internal items only.
	ListWithRowCount findInternalItems(ItemDaoQueryBean itemDaoQueryBean);

	List<InternalItem> findInternalItemsList(ItemDaoQueryBean itemDaoQueryBean);
	
	// Returns internal AND external items.
	ListWithRowCount findInternalAndExternalItems(ItemDaoQueryBean itemDaoQueryBean);
	
	long countInternalItems(ItemDaoQueryBean itemDaoQueryBean);
	
	void updateItem(Item pItem);
	
	void deleteItem(Item pItem);


}
