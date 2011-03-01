package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.ItemDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Item;

public interface ItemDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Item
	Long createItem(Item item);
	
	Item findItem(Long itemId);
	
	ListWithRowCount findItems(ItemDaoQueryBean itemDaoQueryBean);

	List<Item> findItemsList(ItemDaoQueryBean itemDaoQueryBean);
	
	long countItems(ItemDaoQueryBean itemDaoQueryBean);
	
	void updateItem(Item pItem);
	
	void deleteItem(Item pItem);


}
