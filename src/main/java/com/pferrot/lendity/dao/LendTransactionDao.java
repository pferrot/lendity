package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.LendTransactionDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.LendTransaction;

public interface LendTransactionDao {
	
	Long createLendTransaction(LendTransaction pLendTransaction);
	
	void refreshLendTransaction(LendTransaction pLendTransaction);
	
	LendTransaction findLendTransaction(Long pLendTransactionId);
	
	ListWithRowCount findLendTransactions(LendTransactionDaoQueryBean pLendTransactionDaoQueryBean);
	List<LendTransaction> findLendTransactionsList(LendTransactionDaoQueryBean pLendTransactionDaoQueryBean);
	
	long countLendTransactions(LendTransactionDaoQueryBean pLendTransactionDaoQueryBean);
	
	void updateLendTransaction(LendTransaction pLendTransaction);
	
	void deleteLendTransaction(LendTransaction pLendTransaction);
	
	void deleteLendTransactionsForItem(Long pItemId);
	void updateLendTransactionsSetNullItem(Long pItemId);
	
	ListWithRowCount findLendTransactionsWaitingForInput(Long pPersonId, int pFirstResult, int pMaxResults);
	List<LendTransaction> findLendTransactionsWaitingForInputList(Long pPersonId, int pFirstResult, int pMaxResults);
	long countLendTransactionsWaitingForInput(Long pPersonId);
	
	ListWithRowCount findLendTransactionsAsBorrowerWaitingForInput(Long pBorrowerId, int pFirstResult, int pMaxResults);
	List<LendTransaction> findLendTransactionsAsBorrowerWaitingForInputList(Long pBorrowerId, int pFirstResult, int pMaxResults);
	long countLendTransactionsAsBorrowerWaitingForInput(Long pBorrowerId);
	
	ListWithRowCount findLendTransactionsAsLenderWaitingForInput(Long pLenderId, int pFirstResult, int pMaxResults);
	List<LendTransaction> findLendTransactionsAsLenderWaitingForInputList(Long pLenderId, int pFirstResult, int pMaxResults);
	long countLendTransactionsAsLenderWaitingForInput(Long pLenderId);
}
