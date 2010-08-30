package com.pferrot.lendity.dao;

import java.util.Date;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Need;

public interface NeedDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Need
	Long createNeed(Need pNeed);
	
	Need findNeed(Long pNeedId);
	
	// Returns internal items only.
	ListWithRowCount findNeeds(Long[] pOwnerIds, Boolean pOwnerEnabled, String pTitle, Long[] categoriesId, Date pCreationDateMin, String pOrderBy, Boolean pOrderByAscending, int pFirstResult, int pMaxResults);
	
	long countNeeds(Long[] pOwnerIds, Boolean pOwnerEnabled, String pTitle, Long[] categoriesId, Date pCreationDateMin);
	
	void updateNeed(Need pNeed);
	
	void deleteNeed(Need pNeed);
}
