package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.ItemComment;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionComment;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.NeedComment;

public interface CommentDao {
	
	Long createComment(Comment pComment);
	
	Comment findComment(Long pCommentId);
	
	ListWithRowCount findItemComments(InternalItem pInternalItem, int pFirstResult, int pMaxResults);
	List<ItemComment> findItemCommentsList(InternalItem pInternalItem, int pFirstResult, int pMaxResults);
	long countItemComments(InternalItem pInternalItem);
	
	ListWithRowCount findNeedComments(Need pNeed, int pFirstResult, int pMaxResults);
	List<NeedComment> findNeedCommentsList(Need pNeed, int pFirstResult, int pMaxResults);
	long countNeedComments(Need pNeed);
	
	ListWithRowCount findLendTransactionComments(LendTransaction pLendTransaction, int pFirstResult, int pMaxResults);
	List<LendTransactionComment> findLendTransactionCommentsList(LendTransaction pLendTransaction, int pFirstResult, int pMaxResults);
	long countLendTransactionComments(LendTransaction pLendTransaction);
			
	void updateComment(Comment pComment);
	
	void deleteComment(Comment pComment);
}
