package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Item;
import com.pferrot.sharedcalendar.model.Person;

public interface ItemDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Item
	Long createItem(Item item);
	
	Item findItem(Long itemId);
	
	// Return all items containing title.
	List<Item> findItemsByTitle(String title);
	List<Item> findItemsByTitle(String title, int pFirstResult, int pMaxResults);
	
	List<Item> findAllItems();
	
	List<Item> findItems(int pFirstResult, int pMaxResults);
	
	List<Item> findItemsOwnedByUser(User pUser, int pFirstResult, int pMaxResults);
	List<Item> findItemsOwnedByUser(String pUsername, int pFirstResult, int pMaxResults);
	
	List<Item> findItemsOwnedByUsers(String[] pUsernames, int pFirstResult, int pMaxResults);
	List<Item> findItemsByTitleOwnedByUsers(String title, String[] pUsernames , int pFirstResult, int pMaxResults);
	
	List<Item> findItemsOwnedByConnections(Person person, int pFirstResult, int pMaxResults);
	List<Item> findItemsByTitleOwnedByConnections(String title, Person person, int pFirstResult, int pMaxResults);

	List<Item> findItemsBorrowedByUser(User pUser, int pFirstResult, int pMaxResults);
	List<Item> findItemsBorrowedByUser(String pUsername, int pFirstResult, int pMaxResults);
	List<Item> findItemsByTitleBorrowedByUser(String pTitle, String pUsername, int pFirstResult, int pMaxResults);

	List<Item> findItemsLentByUser(User pUser, int pFirstResult, int pMaxResults);
	List<Item> findItemsLentByUser(String pUsername, int pFirstResult, int pMaxResults);
	List<Item> findItemsByTitleLentByUser(String pTitle, String pUsername, int pFirstResult, int pMaxResults);
	
	void updateItem(Item item);
	
	void deleteItem(Item item);


}
