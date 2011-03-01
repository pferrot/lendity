package com.pferrot.lendity.dao;

import java.util.Date;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.NeedDaoQueryBean;
import com.pferrot.lendity.model.Need;

public interface NeedDao {
	
	/////////////////////////////////////////////////////////////////////////////////////////
	// Need
	Long createNeed(Need pNeed);
	
	Need findNeed(Long pNeedId);
	
	ListWithRowCount findNeeds(NeedDaoQueryBean pNeedDaoQueryBean);
	
	long countNeeds(NeedDaoQueryBean pNeedDaoQueryBean);
	
	void updateNeed(Need pNeed);
	
	void deleteNeed(Need pNeed);
}
