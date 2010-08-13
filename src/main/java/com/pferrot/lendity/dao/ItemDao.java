package com.pferrot.lendity.dao;

import java.util.Date;
import java.util.List;

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
	ListWithRowCount findInternalItems(Long[] pOwnerIds, Boolean pOwnerEnabled, Long[] pBorrowerIds, Boolean pBorrowerEnabled,
			String pTitle, Long[] categoriesId, Boolean pVisible, Boolean pBorrowed, Date pCreationDateMin, String pOrderBy, Boolean pOrderByAscending,
			int pFirstResult, int pMaxResults);

	List<InternalItem> findInternalItemsList(Long[] pOwnerIds, Boolean pOwnerEnabled, Long[] pBorrowerIds, Boolean pBorrowerEnabled,
			String pTitle, Long[] categoriesId, Boolean pVisible, Boolean pBorrowed, Date pCreationDateMin, String pOrderBy, Boolean pOrderByAscending, 
			int pFirstResult, int pMaxResults);
	
	// Returns internal AND external items.
	ListWithRowCount findInternalAndExternalItems(Long[] pOwnerIds, Boolean pOwnerEnabled, Long[] pBorrowerIds, Boolean pBorrowerEnabled, String pTitle,
			Long[] categoriesId, Boolean pBorrowed, Date pCreationDateMin, String pOrderBy, Boolean pOrderByAscending,
			int pFirstResult, int pMaxResults);
	
	long countInternalItems(Long[] pOwnerIds, Boolean pOwnerEnabled, Long[] pBorrowerIds, Boolean pBorrowerEnabled, String pTitle, Long[] categoriesId,
			Boolean pVisible, Boolean pBorrowed, Date pCreationDateMin);
	
	void updateItem(Item pItem);
	
	void deleteItem(Item pItem);


}
