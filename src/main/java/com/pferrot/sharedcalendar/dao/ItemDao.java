package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.model.ExternalItem;
import com.pferrot.sharedcalendar.model.InternalItem;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.model.Person;

public interface ItemDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Item
	Long createItem(Item item);
	
	InternalItem findInternalItem(Long itemId);
	ExternalItem findExternalItem(Long itemId);
	
	// Return all internal items containing title.
	List<InternalItem> findAllInternalItems();	
//	List<InternalItem> findItems(int pFirstResult, int pMaxResults);
	
	List<InternalItem> findInternalItems(int pFirstResult, int pMaxResults);
	List<InternalItem> findInternalItemsByTitle(String title);
	List<InternalItem> findInternalItemsByTitle(String title, int pFirstResult, int pMaxResults);
		
	ListWithRowCount findItemsOwnedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
	ListWithRowCount findItemsOwnedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
	ListWithRowCount findItemsByTitleOwnedByPerson(String pTitle, Person pPerson, int pFirstResult, int pMaxResults);
	ListWithRowCount findItemsByTitleOwnedByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
	
	ListWithRowCount findItems(Long[] pPersonIds, String pTitle, Long[] categoriesId, int pFirstResult, int pMaxResults);
	
//	List<InternalItem> findItemsOwnedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
//	long countItemsOwnedByPerson(Person pPerson);
//	List<InternalItem> findItemsOwnedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
//	long countItemsOwnedByPerson(Long pPersonId);
//	List<InternalItem> findItemsByTitleOwnedByPerson(String pTitle, Person pPerson, int pFirstResult, int pMaxResults);
//	long countItemsByTitleOwnedByPerson(String pTitle, Person pPerson);
//	List<InternalItem> findItemsByTitleOwnedByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
//	long countItemsByTitleOwnedByPerson(String pTitle, Long pPersonId);	
	
	List<InternalItem> findVisibleItemsOwnedByPersons(Long[] pPersonIds, int pFirstResult, int pMaxResults);
	List<InternalItem> findVisibleItemsByTitleOwnedByPersons(String pTitle, Long[] pPersonIds, int pFirstResult, int pMaxResults);
	
	List<InternalItem> findVisibleItemsOwnedByConnections(Person pPerson, int pFirstResult, int pMaxResults);
	List<InternalItem> findVisibleItemsByTitleOwnedByConnections(String pTitle, Person pPerson, int pFirstResult, int pMaxResults);

	// Internal and external items can be borrowed.
	List<Item> findItemsBorrowedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
	List<Item> findItemsBorrowedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
	List<Item> findItemsByTitleBorrowedByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);

	List<InternalItem> findItemsLentByPerson(Person pPerson, int pFirstResult, int pMaxResults);
	List<InternalItem> findItemsLentByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
	List<InternalItem> findItemsByTitleLentByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
	
	void updateItem(Item pItem);
	
	void deleteItem(Item pItem);


}
