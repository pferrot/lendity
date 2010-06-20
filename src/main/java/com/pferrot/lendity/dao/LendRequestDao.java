package com.pferrot.lendity.dao;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.LendRequest;

public interface LendRequestDao {
	
	Long createLendRequest(LendRequest pLendRequest);
	
	LendRequest findLendRequest(Long pLendRequestId);
	
	ListWithRowCount findLendRequests(Long pRequesterId, Long pOwnerId, Long pItemId, Boolean pCompleted, int pFirstResult, int pMaxResults);	
	
	void updateLendRequest(LendRequest pLendRequest);
	
	void deleteLendRequest(LendRequest pLendRequest);
	
	void deleteLendRequestsForItem(Long pItemId);
}
