package com.pferrot.lendity.dao;

import java.util.List;

import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ChildComment;
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.model.WallComment;
import com.pferrot.lendity.model.Group;
import com.pferrot.lendity.model.GroupComment;
import com.pferrot.lendity.model.Item;
import com.pferrot.lendity.model.ItemComment;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionComment;
import com.pferrot.lendity.model.Need;
import com.pferrot.lendity.model.NeedComment;

public interface CommentDao {
	
	Long createComment(Comment pComment);
	
	Comment findComment(Long pCommentId);
	ItemComment findItemComment(final Long pCommentId);
	NeedComment findNeedComment(final Long pCommentId);
	GroupComment findGroupComment(final Long pCommentId);
	LendTransactionComment findLendTransactionComment(final Long pCommentId);
	WallComment findWallComment(final Long pCommentId);
	
	ChildComment findChildComment(final Long pCommentId);
	
	ListWithRowCount findWallComments(Long[] pOwnerIds, Boolean pIncludePublicComments, int pFirstResult, int pMaxResults);
	List<WallComment> findWallCommentsList(Long[] pOwnerIds, Boolean pIncludePublicComments, int pFirstResult, int pMaxResults);
	long countWallComments(Long[] pOwnerIds, Boolean pIncludePublicComments);
	
	ListWithRowCount findItemComments(Item pItem, int pFirstResult, int pMaxResults);
	List<ItemComment> findItemCommentsList(Item pItem, int pFirstResult, int pMaxResults);
	long countItemComments(Item pItem);
		
	ListWithRowCount findNeedComments(Need pNeed, int pFirstResult, int pMaxResults);
	List<NeedComment> findNeedCommentsList(Need pNeed, int pFirstResult, int pMaxResults);
	long countNeedComments(Need pNeed);
	
	ListWithRowCount findLendTransactionComments(LendTransaction pLendTransaction, int pFirstResult, int pMaxResults);
	List<LendTransactionComment> findLendTransactionCommentsList(LendTransaction pLendTransaction, int pFirstResult, int pMaxResults);
	long countLendTransactionComments(LendTransaction pLendTransaction);

	ListWithRowCount findGroupComments(Group pGroup, int pFirstResult, int pMaxResults);
	List<GroupComment> findGroupCommentsList(Group pGroup, int pFirstResult, int pMaxResults);
	long countGroupComments(Group pGroup);
	
	ListWithRowCount findChildComments(Comment pParentComment, int pFirstResult, int pMaxResults);
	List<ChildComment> findChildCommentsList(Comment pParentComment, int pFirstResult, int pMaxResults);
	long countChildComments(Comment pParentComment);
			
	void updateComment(Comment pComment);
	
	void deleteComment(Comment pComment);
}
