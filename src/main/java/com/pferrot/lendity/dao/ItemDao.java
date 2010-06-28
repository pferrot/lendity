package com.pferrot.lendity.dao;

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
	
	// Return all internal items containing title.
//	List<InternalItem> findAllInternalItems();	
//	List<InternalItem> findItems(int pFirstResult, int pMaxResults);
	
//	List<InternalItem> findInternalItems(int pFirstResult, int pMaxResults);
//	List<InternalItem> findInternalItemsByTitle(String title);
//	List<InternalItem> findInternalItemsByTitle(String title, int pFirstResult, int pMaxResults);
//		
//	ListWithRowCount findItemsOwnedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
//	ListWithRowCount findItemsOwnedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
//	ListWithRowCount findItemsByTitleOwnedByPerson(String pTitle, Person pPerson, int pFirstResult, int pMaxResults);
//	ListWithRowCount findItemsByTitleOwnedByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
	
	ListWithRowCount findInternalItems(Long[] pOwnerIds, Boolean pOwnerEnabled, Long[] pBorrowerIds, Boolean pBorrowerEnabled, String pTitle, Long[] categoriesId, Boolean pVisible, Boolean pBorrowed, String pOrderBy, Boolean pOrderByAscending, int pFirstResult, int pMaxResults);
	
	long countInternalItems(Long[] pOwnerIds, Boolean pOwnerEnabled, Long[] pBorrowerIds, Boolean pBorrowerEnabled, String pTitle, Long[] categoriesId, Boolean pVisible, Boolean pBorrowed);
	
//	List<InternalItem> findItemsOwnedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
//	long countItemsOwnedByPerson(Person pPerson);
//	List<InternalItem> findItemsOwnedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
//	long countItemsOwnedByPerson(Long pPersonId);
//	List<InternalItem> findItemsByTitleOwnedByPerson(String pTitle, Person pPerson, int pFirstResult, int pMaxResults);
//	long countItemsByTitleOwnedByPerson(String pTitle, Person pPerson);
//	List<InternalItem> findItemsByTitleOwnedByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
//	long countItemsByTitleOwnedByPerson(String pTitle, Long pPersonId);	
	
//	List<InternalItem> findVisibleItemsOwnedByPersons(Long[] pPersonIds, int pFirstResult, int pMaxResults);
//	List<InternalItem> findVisibleItemsByTitleOwnedByPersons(String pTitle, Long[] pPersonIds, int pFirstResult, int pMaxResults);
//	
//	List<InternalItem> findVisibleItemsOwnedByConnections(Person pPerson, int pFirstResult, int pMaxResults);
//	List<InternalItem> findVisibleItemsByTitleOwnedByConnections(String pTitle, Person pPerson, int pFirstResult, int pMaxResults);

	// Internal and external items can be borrowed.
//	List<Item> findItemsBorrowedByPerson(Person pPerson, int pFirstResult, int pMaxResults);
//	List<Item> findItemsBorrowedByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
//	List<Item> findItemsByTitleBorrowedByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
//
//	List<InternalItem> findItemsLentByPerson(Person pPerson, int pFirstResult, int pMaxResults);
//	List<InternalItem> findItemsLentByPerson(Long pPersonId, int pFirstResult, int pMaxResults);
//	List<InternalItem> findItemsByTitleLentByPerson(String pTitle, Long pPersonId, int pFirstResult, int pMaxResults);
	
	void updateItem(Item pItem);
	
	void deleteItem(Item pItem);


}
