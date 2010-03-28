package com.pferrot.sharedcalendar.dao;

import java.util.List;

import com.pferrot.sharedcalendar.model.ConnectionRequest;
import com.pferrot.sharedcalendar.model.Person;

public interface ConnectionRequestDao {
	
	Long createConnectionRequest(ConnectionRequest pConnectionRequest);
	
	ConnectionRequest findConnectionRequest(Long pConnectionRequestId);
	
	List<ConnectionRequest> findConnectionRequestByRequester(Person pRequester, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findConnectionRequestByRequesterAndConnection(Person pPerson1, Person pPerson2, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findUncompletedConnectionRequestByRequesterAndConnection(Person pPerson1, Person pPerson2, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findUncompletedConnectionRequestByRequester(Person pRequester, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findUncompletedConnectionRequestByConnection(Person pRequester, int pFirstResult, int pMaxResults);
	List<ConnectionRequest> findConnectionRequestByConnection(Person pRequester, int pFirstResult, int pMaxResults);
	
	
	void updateConnectionRequest(ConnectionRequest pConnectionRequest);
	
	void deleteConnectionRequest(ConnectionRequest pConnectionRequest);
}
