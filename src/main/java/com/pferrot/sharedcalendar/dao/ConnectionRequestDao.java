package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.sharedcalendar.model.ConnectionRequest;
import com.pferrot.sharedcalendar.model.Person;

public interface ConnectionRequestDao {
	
	Long createConnectionRequest(ConnectionRequest connectionRequest);
	
	ConnectionRequest findConnectionRequest(Long connectionRequestId);
	
	List<ConnectionRequest> findConnectionRequestByRequester(Person requester, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findUncompletedConnectionRequestByRequester(Person requester, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findConnectionRequestByConnection(Person requester, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findUncompletedConnectionRequestByConnection(Person requester, int pFirstResult, int pMaxResults);
	
	void updateConnectionRequest(ConnectionRequest connectionRequest);
	
	void deleteConnectionRequest(ConnectionRequest connectionRequest);
}
