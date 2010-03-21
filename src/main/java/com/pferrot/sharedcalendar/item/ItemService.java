package com.pferrot.sharedcalendar.item;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.sharedcalendar.dao.ItemDao;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.model.ItemCategory;
import com.pferrot.sharedcalendar.model.Language;
import com.pferrot.sharedcalendar.model.ListValue;
import com.pferrot.sharedcalendar.utils.ListValueUtils;

public class ItemService {

	private final static Log log = LogFactory.getLog(ItemService.class);
	
	private ItemDao itemDao;
	private ListValueDao listValueDao;

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public List<ListValue> getCategories() {
		return listValueDao.findListValue(ItemCategory.class);
	}
	
	public Language findLanguage(final String languageLabelCode) {
		return (Language)listValueDao.findListValue(languageLabelCode);
	}

	public Item findItem(final Long itemId) {
		return itemDao.findItem(itemId);
	}
	
	public List<Item> findAllItems() {
		return itemDao.findAllItems();
	}
	
	public List<Item> findItems(final int pFirstResult, final int pMaxResults) {
		return itemDao.findItems(pFirstResult, pMaxResults);
	}
	
	public List<Item> findItemsByTitle(final String pTitle, final int pFirstResult, final int pMaxResults) {
		return itemDao.findItemsByTitle(pTitle, pFirstResult, pMaxResults);
	}
	
	public List<Item> findItemsOwnedByUsername(final String pUsername, final int pFirstResult, final int pMaxResults) {
		return itemDao.findItemsOwnedByUser(pUsername, pFirstResult, pMaxResults);
	}	
	
	public Long createItem(final Item item) {
		return itemDao.createItem(item);
	}

	public Long createItemWithCategories(final Item pItem, final Collection<Long> pCategoriesIds) {
		pItem.setCategories(ListValueUtils.getListValuesFromIds(pCategoriesIds, listValueDao));
		return createItem(pItem);
	}

	public void updateItem(final Item item) {
		itemDao.updateItem(item);
	}

	public void updateItemWithCategories(final Item pItem, final Collection<Long> pCategoriesIds) {
		pItem.setCategories(ListValueUtils.getListValuesFromIds(pCategoriesIds, listValueDao));
		updateItem(pItem);
	}

	public List<Long> getIdsFromItemCategories(final Collection<ItemCategory> itemCategories) {
		return ListValueUtils.getIdsFromListValues(itemCategories);
	}
	
}
