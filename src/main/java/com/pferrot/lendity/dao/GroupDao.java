package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.GroupDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Group;

public interface GroupDao {
	
	Long createGroup(Group pGroup);
	
	Group findGroup(Long pGroupId);
	
	ListWithRowCount findGroups(GroupDaoQueryBean pGroupDaoQueryBean);
	List<Group> findGroupsList(GroupDaoQueryBean pGroupDaoQueryBean);
	long countGroups(GroupDaoQueryBean pGroupDaoQueryBean);
		
	void updateGroup(Group pGroup);
	
	void deleteGroup(Group pGroup);
}
