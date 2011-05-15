package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.GroupJoinRequestDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.GroupJoinRequest;

public interface GroupJoinRequestDao {
	
	Long createGroupJoinRequest(GroupJoinRequest pGroupJoinRequest);
	
	GroupJoinRequest findGroupJoinRequest(Long pGroupJoinRequestId);
	
	ListWithRowCount findGroupJoinRequests(GroupJoinRequestDaoQueryBean pQueryBean);

	List<GroupJoinRequest> findGroupJoinRequestsList(GroupJoinRequestDaoQueryBean pQueryBean);	
	
	long countGroupJoinRequests(GroupJoinRequestDaoQueryBean pQueryBean);
	
	void updateGroupJoinRequest(GroupJoinRequest pGroupJoinRequest);
	
	void deleteGroupJoinRequest(GroupJoinRequest pGroupJoinRequest);
}
