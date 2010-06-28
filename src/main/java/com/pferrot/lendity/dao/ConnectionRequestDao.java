package com.pferrot.lendity.dao;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ConnectionRequest;

public interface ConnectionRequestDao {
	
	Long createConnectionRequest(ConnectionRequest pConnectionRequest);
	
	ConnectionRequest findConnectionRequest(Long pConnectionRequestId);
	
	ListWithRowCount findConnectionRequests(Long pConnectionId, Long pRequesterId, Boolean pCompleted, int pFirstResult, int pMaxResults);	
	
	long countConnectionRequests(Long pConnectionId, Long pRequesterId, Boolean pCompleted);
	
	void updateConnectionRequest(ConnectionRequest pConnectionRequest);
	
	void deleteConnectionRequest(ConnectionRequest pConnectionRequest);
}
