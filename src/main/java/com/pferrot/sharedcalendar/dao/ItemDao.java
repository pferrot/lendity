package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.model.Person;

public interface ItemDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Item
	Long createItem(Item item);
	
	Item findItem(Long itemId);
	
	// Return all items containing title.
	List<Item> findAllItems();	
	List<Item> findItems(int pFirstResult, int pMaxResults);
	
	List<Item> findItemsByTitle(String title);
	List<Item> findItemsByTitle(String title, int pFirstResult, int pMaxResults);
		
	List<Item> findItemsOwnedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
	List<Item> findItemsOwnedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
	
	List<Item> findVisibleItemsOwnedByPersons(Long[] pPersonIds, int pFirstResult, int pMaxResults);
	List<Item> findVisibleItemsByTitleOwnedByPersons(String pTitle, Long[] pPersonIds, int pFirstResult, int pMaxResults);
	
	List<Item> findVisibleItemsOwnedByConnections(Person pPerson, int pFirstResult, int pMaxResults);
	List<Item> findVisibleItemsByTitleOwnedByConnections(String pTitle, Person pPerson, int pFirstResult, int pMaxResults);

	List<Item> findItemsBorrowedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
	List<Item> findItemsBorrowedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
	List<Item> findItemsByTitleBorrowedByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);

	List<Item> findItemsLentByPerson(Person pPerson, int pFirstResult, int pMaxResults);
	List<Item> findItemsLentByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
	List<Item> findItemsByTitleLentByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
	
	void updateItem(Item pItem);
	
	void deleteItem(Item pItem);


}
