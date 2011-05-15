package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.NeedDaoQueryBean;
import com.pferrot.lendity.model.Need;

public interface NeedDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Need
	Long createNeed(Need pNeed);
	
	Need findNeed(Long pNeedId);
	
	ListWithRowCount findNeeds(NeedDaoQueryBean pNeedDaoQueryBean);
	List<Need> findNeedsList(NeedDaoQueryBean pNeedDaoQueryBean);
	
	long countNeeds(NeedDaoQueryBean pNeedDaoQueryBean);
	
	void updateNeed(Need pNeed);
	
	void deleteNeed(Need pNeed);
}
