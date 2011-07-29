package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.PotentialConnectionDaoQueryBean;
import com.pferrot.lendity.model.PotentialConnection;


public interface PotentialConnectionDao {
	
	Long createPotentialConnection(PotentialConnection potentialConnection);
	
	PotentialConnection findPotentialConnection(Long potentialConnectionId);
	ListWithRowCount findPotentialConnections(PotentialConnectionDaoQueryBean queryBean);
	List<PotentialConnection> findPotentialConnectionsList(PotentialConnectionDaoQueryBean queryBean);
	
	void updatePotentialConnection(PotentialConnection potentialConnection);
	
	void deletePotentialConnection(PotentialConnection potentialConnection);
	void deletePotentialConnectionsForPerson(Long personId);
	void deletePotentialConnectionForPersonAndConnection(Long personId, Long connectionId);
	
	long countPotentialConnections(PotentialConnectionDaoQueryBean queryBean);
	
}
