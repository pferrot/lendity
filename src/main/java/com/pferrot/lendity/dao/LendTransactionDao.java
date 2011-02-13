package com.pferrot.lendity.dao;

import com.pferrot.lendity.dao.bean.LendTransactionDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.LendTransaction;

public interface LendTransactionDao {
	
	Long createLendTransaction(LendTransaction pLendTransaction);
	
	LendTransaction findLendTransaction(Long pLendTransactionId);
	
	ListWithRowCount findLendTransactions(LendTransactionDaoQueryBean pLendTransactionDaoQueryBean);	
	
	long countLendTransactions(LendTransactionDaoQueryBean pLendTransactionDaoQueryBean);
	
	void updateLendTransaction(LendTransaction pLendTransaction);
	
	void deleteLendTransaction(LendTransaction pLendTransaction);
	
	void deleteLendTransactionsForItem(Long pItemId);
}
