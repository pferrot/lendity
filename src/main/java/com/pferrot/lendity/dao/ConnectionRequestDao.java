package com.pferrot.lendity.dao;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ConnectionRequest;

public interface ConnectionRequestDao {
	
	Long createConnectionRequest(ConnectionRequest pConnectionRequest);
	
	ConnectionRequest findConnectionRequest(Long pConnectionRequestId);
	
	ListWithRowCount findConnectionRequests(Long[] pConnectionIds, Long[] pRequesterIds, Boolean pCompleted, Long[] pResponseIds, int pFirstResult, int pMaxResults);	
	
	long countConnectionRequests(Long[] pConnectionIds, Long[] pRequesterIds, Boolean pCompleted, Long[] pResponseIds);
	
	void updateConnectionRequest(ConnectionRequest pConnectionRequest);
	
	void deleteConnectionRequest(ConnectionRequest pConnectionRequest);
}
