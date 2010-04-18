package com.pferrot.sharedcalendar.item;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.dao.ItemDao;
import com.pferrot.sharedcalendar.dao.ListValueDao;
import com.pferrot.sharedcalendar.dao.PersonDao;
import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.model.ExternalItem;
import com.pferrot.sharedcalendar.model.InternalItem;
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

	public InternalItem findInternalItem(final Long itemId) {
		return itemDao.findInternalItem(itemId);
	}

	public ExternalItem findExternalItem(final Long itemId) {
		return itemDao.findExternalItem(itemId);
	}
	
//	public List<InternalItem> findAllInternalItems() {
//		return itemDao.findAllInternalItems();
//	}
//	
//	public List<InternalItem> findItems(final int pFirstResult, final int pMaxResults) {
//		return itemDao.findInternalItems(pFirstResult, pMaxResults);
//	}
//	
//	public List<InternalItem> findItemsByTitle(final String pTitle, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findInternalItemsByTitle(pTitle, pFirstResult, pMaxResults);
//	}
	
//	public ListWithRowCount findItemsOwnedByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsOwnedByPerson(pPersonId, pFirstResult, pMaxResults);
//	}
//
//	public ListWithRowCount findItemsByTitleOwnedByPersonId(final String pTitle, final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsByTitleOwnedByPerson(pTitle, pPersonId, pFirstResult, pMaxResults);
//	}

	public ListWithRowCount findItems(final Long pOwnerId, final String pTitle, final Long pCategoryId, final Boolean pVisible,
			final Boolean pBorrowed, final int pFirstResult, final int pMaxResults) {
		Long[] personIds = null;
		if (pOwnerId != null) {
			personIds = new Long[1];
			personIds[0] = pOwnerId;
		}
		Long[] categoryIds = null;
		if (pCategoryId != null) {
			categoryIds = new Long[1];
			categoryIds[0] = pCategoryId;
		}
		
		return itemDao.findItems(personIds, null, pTitle, categoryIds, pVisible, pBorrowed, pFirstResult, pMaxResults);
	}

//	public List<Item> findItemsBorrowedByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsBorrowedByPerson(pPersonId, pFirstResult, pMaxResults);
//	}
//
//	public List<InternalItem> findItemsLentByPersonId(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
//		return itemDao.findItemsLentByPerson(pPersonId, pFirstResult, pMaxResults);
//	}
//	
//	public List<InternalItem> findVisibleItemsOwnedByCurrentPersonConnections(final int pFirstResult, final int pMaxResults) {
//		return itemDao.findVisibleItemsOwnedByConnections(getCurrentPerson(), pFirstResult, pMaxResults);
//	}

	public Long createItem(final Item item) {
		return itemDao.createItem(item);
	}

	public Long createItemWithCategory(final Item pItem, final Long pCategoryId) {
		pItem.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, listValueDao));
		return createItem(pItem);
	}

	public void updateItem(final Item item) {
		itemDao.updateItem(item);
	}

	public void updateItemWithCategory(final Item pItem, final Long pCategoryId) {
		pItem.setCategory((ItemCategory) ListValueUtils.getListValueFromId(pCategoryId, listValueDao));
		updateItem(pItem);
	}

	public boolean isCurrentUserAuthorizedToView(final Item pItem) {
		CoreUtils.assertNotNull(pItem);
		if (isCurrentUserAuthorizedToEdit(pItem)) {
			return true;
		}
		// Connections can view.
		if (pItem instanceof InternalItem) {
			final InternalItem internalItem = (InternalItem) pItem;
			if (internalItem.isVisible() && 
				internalItem.getOwner() != null &&
				internalItem.getOwner().getConnections() != null &&
				internalItem.getOwner().getConnections().contains(getCurrentPerson())) {
				return true;
			}
		}
		return false;
	}

	public boolean isCurrentUserAuthorizedToEdit(final Item pItem) {
		CoreUtils.assertNotNull(pItem);
		final Person currentPerson = getCurrentPerson();
		if (currentPerson == null) {
			return false;
		}
		if (currentPerson.getUser() != null &&
		    currentPerson.getUser().isAdmin()) {
			return true;
		}
		if (pItem instanceof InternalItem) {
			final InternalItem internalItem = (InternalItem) pItem;
			if (currentPerson.equals(internalItem.getOwner())) {
				return true;
			}			
		}
		else if (pItem instanceof ExternalItem) {
			final ExternalItem externalItem = (ExternalItem) pItem;
			if (currentPerson.equals(externalItem.getBorrower())) {
				return true;
			}
		}
		return false;
	}

	public Person getCurrentPerson() {
		return personDao.findPerson(PersonUtils.getCurrentPersonId());
	}
	
}
