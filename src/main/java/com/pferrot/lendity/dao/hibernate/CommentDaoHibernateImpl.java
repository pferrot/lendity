package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.lendity.dao.CommentDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Comment;
import com.pferrot.lendity.model.InternalItem;
import com.pferrot.lendity.model.ItemComment;
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
	
	public List<ItemComment> findItemCommentsList(final InternalItem pInternalItem, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getItemCommentsDetachedCriteria(pInternalItem);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public List<NeedComment> findNeedCommentsList(final Need pNeed, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getNeedCommentsDetachedCriteria(pNeed);
		criteria.addOrder(Order.desc("creationDate"));		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public long countItemComments(final InternalItem pInternalItem) {
		final DetachedCriteria criteria = getItemCommentsDetachedCriteria(pInternalItem);
		return rowCount(criteria);
	}
	
	public long countNeedComments(final Need pNeed) {
		final DetachedCriteria criteria = getNeedCommentsDetachedCriteria(pNeed);
		return rowCount(criteria);
	}

	private DetachedCriteria getItemCommentsDetachedCriteria(final InternalItem pInternalItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ItemComment.class);
	
		if (pInternalItem != null) {
			criteria.add(Restrictions.eq("item", pInternalItem));
		}		
		return criteria;	
	}
	
	private DetachedCriteria getNeedCommentsDetachedCriteria(final Need pNeed) {
		DetachedCriteria criteria = DetachedCriteria.forClass(NeedComment.class);
	
		if (pNeed != null) {
			criteria.add(Restrictions.eq("need", pNeed));
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

	public ListWithRowCount findItemComments(final InternalItem pInternalItem, final int pFirstResult, final int pMaxResults) {
		final List list = findItemCommentsList(pInternalItem, pFirstResult, pMaxResults);
		final long count = countItemComments(pInternalItem);
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findNeedComments(final Need pNeed, final int pFirstResult, final int pMaxResults) {
		final List list = findNeedCommentsList(pNeed, pFirstResult, pMaxResults);
		final long count = countNeedComments(pNeed);
		return new ListWithRowCount(list, count);
	}
}
