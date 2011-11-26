package com.pferrot.lendity.dao;

import java.util.Date;
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
	ItemComment findItemComment(Long pCommentId);
	NeedComment findNeedComment(Long pCommentId);
	GroupComment findGroupComment(Long pCommentId);
	LendTransactionComment findLendTransactionComment(Long pCommentId);
	WallComment findWallComment(Long pCommentId);
	
	ChildComment findChildComment(Long pCommentId);
	
	ListWithRowCount findOwnWallComments(Long pPersonId, Long[] pConnectionIds, Long[] pConnectionWithVisibleCommentsOnWallIds, Boolean pIncludeAdminPublicComments, int pFirstResult, int pMaxResults);
	List<WallComment> findOwnWallCommentsList(Long pPersonId, Long[] pConnectionIds, Long[] pConnectionWithVisibleCommentsOnWallIds,  Boolean pIncludeAdminPublicComments, int pFirstResult, int pMaxResults);
	long countOwnWallComments(Long pPersonId, Long[] pConnectionIds, Long[] pConnectionWithVisibleCommentsOnWallIds, Boolean pIncludeAdminPublicComments);
	
	ListWithRowCount findOtherWallComments(Long pWallOwnerId, Long pVisitorId, Boolean pIncludeWallOwnerPrivateComments, Boolean pIncludeOtherPublicCommentsWithOwner, int pFirstResult, int pMaxResults);
	List<WallComment> findOtherWallCommentsList(Long pWallOwnerId, Long pVisitorId, Boolean pIncludeWallOwnerPrivateComments, Boolean pIncludeOtherPublicCommentsWithOwner, int pFirstResult, int pMaxResults);
	long countOtherWallComments(Long pWallOwnerId, Long pVisitorId, Boolean pIncludeWallOwnerPrivateComments, Boolean pIncludeOtherPublicCommentsWithOwner);
	
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
	
	ListWithRowCount findChildComments(Comment pParentComment, Date pMaxDate, int pFirstResult, int pMaxResults);
	List<ChildComment> findChildCommentsList(Comment pParentComment, Date pMaxDate, int pFirstResult, int pMaxResults);
	long countChildComments(Comment pParentComment, Date pMaxDate);
	
	
	ListWithRowCount findChildComments(Long pParentCommentId, Date pMaxDate, int pFirstResult, int pMaxResults);
	List<ChildComment> findChildCommentsList(Long pParentCommentId, Date pMaxDate, int pFirstResult, int pMaxResults);
	long countChildComments(Long pParentCommentId, Date pMaxDate);
	
	void updateComment(Comment pComment);
	
	void deleteComment(Comment pComment);
}
