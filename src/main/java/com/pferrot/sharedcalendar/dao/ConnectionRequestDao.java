package com.pferrot.sharedcalendar.dao;

import com.pferrot.sharedcalendar.dao.bean.ListWithRowCount;
import com.pferrot.sharedcalendar.model.ConnectionRequest;

public interface ConnectionRequestDao {
	
	Long createConnectionRequest(ConnectionRequest pConnectionRequest);
	
	ConnectionRequest findConnectionRequest(Long pConnectionRequestId);
	
	ListWithRowCount findConnectionRequests(Long pConnectionId, Long pRequesterId, Boolean pCompleted, int pFirstResult, int pMaxResults);	
	
	void updateConnectionRequest(ConnectionRequest pConnectionRequest);
	
	void deleteConnectionRequest(ConnectionRequest pConnectionRequest);
}
