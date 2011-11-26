package com.pferrot.lendity.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.lendity.dao.CommentDao;
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

public class CommentDaoHibernateImpl extends HibernateDaoSupport implements CommentDao {

	public Long createComment(final Comment pComment) {
		return (Long)getHibernateTemplate().save(pComment);
	}

	public void deleteComment(final Comment pComment) {
		getHibernateTemplate().delete(pComment);		
	}

	public void updateComment(final Comment pComment) {
		getHibernateTemplate().update(pComment);
	}

	public Comment findComment(final Long pCommentId) {
		return (Comment)getHibernateTemplate().load(Comment.class, pCommentId);
	}
	
	public ItemComment findItemComment(final Long pCommentId) {
		return (ItemComment)getHibernateTemplate().load(ItemComment.class, pCommentId);
	}
	
	public NeedComment findNeedComment(final Long pCommentId) {
		return (NeedComment)getHibernateTemplate().load(NeedComment.class, pCommentId);
	}
	
	public GroupComment findGroupComment(final Long pCommentId) {
		return (GroupComment)getHibernateTemplate().load(GroupComment.class, pCommentId);
	}
	
	public LendTransactionComment findLendTransactionComment(final Long pCommentId) {
		return (LendTransactionComment)getHibernateTemplate().load(LendTransactionComment.class, pCommentId);
	}
	
	public WallComment findWallComment(final Long pCommentId) {
		return (WallComment)getHibernateTemplate().load(WallComment.class, pCommentId);
	}
	
	public ChildComment findChildComment(final Long pCommentId) {
		return (ChildComment)getHibernateTemplate().load(ChildComment.class, pCommentId);
	}
	
	public List<ItemComment> findItemCommentsList(final Item pItem, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getItemCommentsDetachedCriteria(pItem);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}
	
	public List<WallComment> findOwnWallCommentsList(final Long pOwnerId, final Long[] pConnectionIds, final Long[] pConnectionWithVisibleCommentsOnWallIds, final Boolean pIncludeAdminPublicComments, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getOwnWallCommentsDetachedCriteria(pOwnerId, pConnectionIds, pConnectionWithVisibleCommentsOnWallIds, pIncludeAdminPublicComments, null);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}
	
	public List<WallComment> findOtherWallCommentsList(final Long pWallOwnerId, final Long pVisitorId, final Boolean pIncludeWallOwnerPrivateComments, final Boolean pIncludeOtherPublicCommentsWithOwner, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getOtherWallCommentsDetachedCriteria(pWallOwnerId, pVisitorId, pIncludeWallOwnerPrivateComments, pIncludeOtherPublicCommentsWithOwner);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public List<NeedComment> findNeedCommentsList(final Need pNeed, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getNeedCommentsDetachedCriteria(pNeed);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public List<LendTransactionComment> findLendTransactionCommentsList(final LendTransaction pLendTransaction, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getLendTransactionCommentsDetachedCriteria(pLendTransaction);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public List<GroupComment> findGroupCommentsList(final Group pGroup, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getGroupCommentsDetachedCriteria(pGroup);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}
	
	public List<ChildComment> findChildCommentsList(final Long pParentCommentId, final Date pMaxDate, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getChildCommentsDetachedCriteria(pParentCommentId, pMaxDate);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}
	
	public List<ChildComment> findChildCommentsList(final Comment pParentComment, final Date pMaxDate, final int pFirstResult, final int pMaxResults) {
		Long parentCommentId = null;
		if (pParentComment != null) {
			parentCommentId = pParentComment.getId(); 
		}		
		return findChildCommentsList(parentCommentId, pMaxDate, pFirstResult, pMaxResults);		
	}

	public long countItemComments(final Item pItem) {
		final DetachedCriteria criteria = getItemCommentsDetachedCriteria(pItem);
		return rowCount(criteria);
	}
	
	public long countOwnWallComments(final Long pOwnerId, final Long[] pConnectionIds, final Long[] pConnectionWithVisibleCommentsOnWallIds, final Boolean pIncludeAdminPublicComments) {
		final DetachedCriteria criteria = getOwnWallCommentsDetachedCriteria(pOwnerId, pConnectionIds, pConnectionWithVisibleCommentsOnWallIds, pIncludeAdminPublicComments, null);
		return rowCount(criteria);
	}
	
	public long countOtherWallComments(final Long pWallOwnerId, final Long pVisitorId, final Boolean pIncludeWallOwnerPrivateComments, final Boolean pIncludeOtherPublicCommentsWithOwner) {
		final DetachedCriteria criteria = getOtherWallCommentsDetachedCriteria(pWallOwnerId, pVisitorId, pIncludeWallOwnerPrivateComments, pIncludeOtherPublicCommentsWithOwner);
		return rowCount(criteria);
	}
	
	public long countNeedComments(final Need pNeed) {
		final DetachedCriteria criteria = getNeedCommentsDetachedCriteria(pNeed);
		return rowCount(criteria);
	}

	public long countLendTransactionComments(final LendTransaction pLendTransaction) {
		final DetachedCriteria criteria = getLendTransactionCommentsDetachedCriteria(pLendTransaction);
		return rowCount(criteria);
	}
	
	public long countGroupComments(final Group pGroup) {
		final DetachedCriteria criteria = getGroupCommentsDetachedCriteria(pGroup);
		return rowCount(criteria);
	}
	
	public long countChildComments(final Long pParentCommentId, final Date pMaxDate) {
		final DetachedCriteria criteria = getChildCommentsDetachedCriteria(pParentCommentId, pMaxDate);
		return rowCount(criteria);
	}
	
	public long countChildComments(final Comment pParentComment, final Date pMaxDate) {
		Long parentCommentId = null;
		if (pParentComment != null) {
			parentCommentId = pParentComment.getId(); 
		}
		return countChildComments(parentCommentId, pMaxDate);
	}
	
	

	private DetachedCriteria getItemCommentsDetachedCriteria(final Item pItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ItemComment.class);
	
		if (pItem != null) {
			criteria.add(Restrictions.eq("item", pItem));
		}		
		return criteria;	
	}
	
	private DetachedCriteria getOwnWallCommentsDetachedCriteria(final Long pPersonId, final Long[] pConnectionIds, final Long[] pConnectionWithVisibleCommentsOnWallIds, final Boolean pIncludeAdminPublicComments,
			final Boolean pOnlyShowPrivateComments) {
		
		CoreUtils.assertNotNull(pPersonId);
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(WallComment.class);
	
		Criterion finalCriterion = null;
		// Own comments on own wall.
		final Criterion ownCommentsCriterion =	Restrictions.eq("owner.id", pPersonId);
		final Criterion otherCommentsCriterion =	Restrictions.eq("wallOwner.id", pPersonId);
		
		finalCriterion = Restrictions.or(ownCommentsCriterion, otherCommentsCriterion);
		
		if (pConnectionIds != null && pConnectionIds.length > 0) {
			finalCriterion = Restrictions.or(
								finalCriterion,
								Restrictions.and(
										Restrictions.in("owner.id", pConnectionIds),
										Restrictions.and(
												Restrictions.eq("privateComment", Boolean.FALSE),
												Restrictions.isNull("wallOwner"))
										));
		}
		
		if (pConnectionWithVisibleCommentsOnWallIds != null && pConnectionWithVisibleCommentsOnWallIds.length > 0) {
			finalCriterion = Restrictions.or(
					finalCriterion,
					Restrictions.and(
							Restrictions.in("wallOwner.id", pConnectionWithVisibleCommentsOnWallIds),
							Restrictions.and(
								Restrictions.in("owner.id", pConnectionIds),
								Restrictions.eq("privateComment", Boolean.FALSE)
							)
					));
		}
					
		if (Boolean.TRUE.equals(pIncludeAdminPublicComments)) {
			final Criterion adminPublicCriterion = Restrictions.and(
					Restrictions.eq("publicComment", Boolean.TRUE),
					Restrictions.eq("adminComment", Boolean.TRUE)); 
			finalCriterion = Restrictions.or(finalCriterion, adminPublicCriterion);
		}
		
		if (Boolean.TRUE.equals(pOnlyShowPrivateComments)) {
			finalCriterion = Restrictions.and(finalCriterion, Restrictions.eq("privateComment", Boolean.TRUE));
		}
		
		criteria.add(finalCriterion);
				
		return criteria;	
	}
	
	private DetachedCriteria getOtherWallCommentsDetachedCriteria(final Long pWallOwnerId, final Long pVisitorId, final Boolean pIncludeWallOwnerPrivateComments, final Boolean pIncludeOtherPublicCommentsWithOwner) {
		CoreUtils.assertNotNull(pWallOwnerId);
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(WallComment.class);
		
		Criterion finalCriterion = null;
		
		Criterion ownerComments = null;
		if (Boolean.TRUE.equals(pIncludeWallOwnerPrivateComments)) {
			ownerComments = Restrictions.and(
								Restrictions.eq("owner.id", pWallOwnerId),
								Restrictions.isNull("wallOwner")
							);
		}
		// Do not include wall owner private comments.
		else {
			ownerComments = Restrictions.and(
								Restrictions.eq("owner.id", pWallOwnerId),
								Restrictions.and(
									Restrictions.eq("publicComment", Boolean.TRUE),
									Restrictions.isNull("wallOwner")
								)
							);	
		}
		
		finalCriterion = ownerComments;
		
		if (pVisitorId != null) {
			final Criterion visitorCommentsToOwner =	Restrictions.and(
															Restrictions.eq("owner.id", pVisitorId),
															Restrictions.eq("wallOwner.id", pWallOwnerId));
			finalCriterion = Restrictions.or(
					finalCriterion,
					visitorCommentsToOwner);
		}

		
		
		if (Boolean.TRUE.equals(pIncludeOtherPublicCommentsWithOwner)) {
			// Comments 
			final Criterion otherPublicCommentsWithOwner = 	Restrictions.and(
																Restrictions.eq("wallOwner.id", pWallOwnerId),
																Restrictions.eq("privateComment", Boolean.FALSE)
															);
			finalCriterion = Restrictions.or(finalCriterion, otherPublicCommentsWithOwner);
		}
		
		criteria.add(finalCriterion);

		return criteria;
	}
	
	private DetachedCriteria getNeedCommentsDetachedCriteria(final Need pNeed) {
		DetachedCriteria criteria = DetachedCriteria.forClass(NeedComment.class);
	
		if (pNeed != null) {
			criteria.add(Restrictions.eq("need", pNeed));
		}		
		return criteria;	
	}

	private DetachedCriteria getLendTransactionCommentsDetachedCriteria(final LendTransaction pLendTransaction) {
		DetachedCriteria criteria = DetachedCriteria.forClass(LendTransactionComment.class);
	
		if (pLendTransaction != null) {
			criteria.add(Restrictions.eq("lendTransaction", pLendTransaction));
		}		
		return criteria;	
	}

	private DetachedCriteria getGroupCommentsDetachedCriteria(final Group pGroup) {
		DetachedCriteria criteria = DetachedCriteria.forClass(GroupComment.class);
	
		if (pGroup != null) {
			criteria.add(Restrictions.eq("group", pGroup));
		}		
		return criteria;	
	}
	
	private DetachedCriteria getChildCommentsDetachedCriteria(final Long pParentCommentId, final Date pMaxDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ChildComment.class);
	
		if (pParentCommentId != null) {
			criteria.add(Restrictions.eq("parentComment.id", pParentCommentId));
		}		
		if (pMaxDate != null) {
			criteria.add(Restrictions.lt("creationDate", pMaxDate));
		}
		return criteria;	
	}

	/**
	 * Returns the number of rows for a giver DetachedCriteria.
	 *
	 * @param pCriteria
	 * @return
	 */
	private long rowCount(final DetachedCriteria pCriteria) {
		pCriteria.setProjection(Projections.rowCount());
		return ((Long)getHibernateTemplate().findByCriteria(pCriteria).get(0)).longValue();
	}

	public ListWithRowCount findItemComments(final Item pItem, final int pFirstResult, final int pMaxResults) {
		final List list = findItemCommentsList(pItem, pFirstResult, pMaxResults);
		final long count = countItemComments(pItem);
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findOwnWallComments(final Long pOwnerId, final Long[] pConnectionIds, final Long[] pConnectionWithVisibleCommentsOnWallIds, final Boolean pIncludeAdminPublicComments, final int pFirstResult, final int pMaxResults) {
		final List list = findOwnWallCommentsList(pOwnerId, pConnectionIds, pConnectionWithVisibleCommentsOnWallIds, pIncludeAdminPublicComments, pFirstResult, pMaxResults);
		final long count = countOwnWallComments(pOwnerId, pConnectionIds, pConnectionWithVisibleCommentsOnWallIds, pIncludeAdminPublicComments);
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findOtherWallComments(final Long pWallOwnerId, final Long pVisitorId, final Boolean pIncludeWallOwnerPrivateComments, final Boolean pIncludeOtherPublicCommentsWithOwner, int pFirstResult, final int pMaxResults) {
		final List list = findOtherWallCommentsList(pWallOwnerId, pVisitorId, pIncludeWallOwnerPrivateComments, pIncludeOtherPublicCommentsWithOwner, pFirstResult, pMaxResults);
		final long count = countOtherWallComments(pWallOwnerId, pVisitorId, pIncludeWallOwnerPrivateComments, pIncludeOtherPublicCommentsWithOwner);
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findNeedComments(final Need pNeed, final int pFirstResult, final int pMaxResults) {
		final List list = findNeedCommentsList(pNeed, pFirstResult, pMaxResults);
		final long count = countNeedComments(pNeed);
		return new ListWithRowCount(list, count);
	}

	public ListWithRowCount findLendTransactionComments(final LendTransaction pLendTransaction, final int pFirstResult, final int pMaxResults) {
		final List list = findLendTransactionCommentsList(pLendTransaction, pFirstResult, pMaxResults);
		final long count = countLendTransactionComments(pLendTransaction);
		return new ListWithRowCount(list, count);
	}

	public ListWithRowCount findGroupComments(final Group pGroup, final int pFirstResult, final int pMaxResults) {
		final List list = findGroupCommentsList(pGroup, pFirstResult, pMaxResults);
		final long count = countGroupComments(pGroup);
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findChildComments(final Long pParentCommentId, final Date pMaxDate, final int pFirstResult, final int pMaxResults) {
		final List list = findChildCommentsList(pParentCommentId, pMaxDate, pFirstResult, pMaxResults);
		final long count = countChildComments(pParentCommentId, pMaxDate);
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findChildComments(final Comment pParentComment, final Date pMaxDate, final int pFirstResult, final int pMaxResults) {
		final List list = findChildCommentsList(pParentComment, pMaxDate, pFirstResult, pMaxResults);
		final long count = countChildComments(pParentComment, pMaxDate);
		return new ListWithRowCount(list, count);
	}
}
