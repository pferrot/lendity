package com.pferrot.lendity.dao;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ConnectionRequest;

public interface ConnectionRequestDao {
	
	Long createConnectionRequest(ConnectionRequest pConnectionRequest);
	
	ConnectionRequest findConnectionRequest(Long pConnectionRequestId);
	
	/**
	 * 
	 * @param pConnectionIds: IDs of the connections, all if null, 
	 * @param pRequesterIds: IDs of the requesters, all if null.
	 * @param pOrCriteria: if TRUE, then pConnectionIds and pRequesterIds will be dealt with as OR - otherwise a AND is made.
	 * @param pExcludedConnectionIds: Connection requests with those connections will be excluded. 
	 * @param pExcludedRequesterIds: Connection requests with those requesters will be excluded.
	 * @param pCompleted: only returns completed connection requests if TRUE
	 * @param pResponseIds: IDs of the responses, all if null
	 * @param pOrderByField: responseDate or requestDate
	 * @param pOrderByAsc 
	 * @param pFirstResult
	 * @param pMaxResults
	 * @return
	 */
	ListWithRowCount findConnectionRequests(Long[] pConnectionIds, Long[] pRequesterIds, Boolean pOrCriteria, Long[] pExcludedConnectionIds, Long[] pExcludedRequesterIds, Boolean pCompleted, Long[] pResponseIds, String pOrderByField, Boolean pOrderByAsc, int pFirstResult, int pMaxResults);	
	
	long countConnectionRequests(Long[] pConnectionIds, Long[] pRequesterIds, Boolean pOrCriteria, Long[] pExcludedConnectionIds, Long[] pExcludedRequesterIds, Boolean pCompleted, Long[] pResponseIds);
	
	void updateConnectionRequest(ConnectionRequest pConnectionRequest);
	
	void deleteConnectionRequest(ConnectionRequest pConnectionRequest);
}
