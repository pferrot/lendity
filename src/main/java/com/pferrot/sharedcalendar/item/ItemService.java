package com.pferrot.sharedcalendar.item;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.dao.ItemDao;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.model.ItemCategory;
import com.pferrot.sharedcalendar.model.Language;
import com.pferrot.sharedcalendar.model.ListValue;
import com.pferrot.sharedcalendar.model.Person;
import com.pferrot.sharedcalendar.person.PersonUtils;
import com.pferrot.sharedcalendar.utils.ListValueUtils;

public class ItemService {

	private final static Log log = LogFactory.getLog(ItemService.class);
	
	private ItemDao itemDao;
	private ListValueDao listValueDao;
	private PersonDao personDao;

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
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
	
	public List<Item> findItemsOwnedByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		return itemDao.findItemsOwnedByPerson(pPersonId, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsBorrowedByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		return itemDao.findItemsBorrowedByPerson(pPersonId, pFirstResult, pMaxResults);
	}

	public List<Item> findItemsLentByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		return itemDao.findItemsLentByPerson(pPersonId, pFirstResult, pMaxResults);
	}
	
	public List<Item> findVisibleItemsOwnedByCurrentPersonConnections(final int pFirstResult, final int pMaxResults) {
		return itemDao.findVisibleItemsOwnedByConnections(getCurrentPerson(), pFirstResult, pMaxResults);
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

	public boolean isCurrentUserAuthorizedToView(final Item pItem) {
		CoreUtils.assertNotNull(pItem);
		if (isCurrentUserAuthorizedToEdit(pItem)) {
			return true;
		}
		// Connections can view.
		if (pItem.isVisible() && 
		    pItem.getOwner() != null &&
		    pItem.getOwner().getConnections() != null &&
		    pItem.getOwner().getConnections().contains(getCurrentPerson())) {
			return true;
		}
		return false;
	}

	public boolean isCurrentUserAuthorizedToEdit(final Item pItem) {
		CoreUtils.assertNotNull(pItem);
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		if (currentPerson.equals(pItem.getOwner())) {
			return true;
		}
		if (currentPerson.getUser() != null &&
		    currentPerson.getUser().isAdmin()) {
			return true;
		}
		return false;
	}

	public Person getCurrentPerson() {
		return personDao.findPerson(PersonUtils.getCurrentPersonId());
	}
	
}
