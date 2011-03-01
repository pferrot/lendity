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
	
	public List<ItemComment> findItemCommentsList(final Item pItem, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getItemCommentsDetachedCriteria(pItem);
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

	public long countItemComments(final Item pItem) {
		final DetachedCriteria criteria = getItemCommentsDetachedCriteria(pItem);
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

	private DetachedCriteria getItemCommentsDetachedCriteria(final Item pItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ItemComment.class);
	
		if (pItem != null) {
			criteria.add(Restrictions.eq("item", pItem));
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

	private DetachedCriteria getLendTransactionCommentsDetachedCriteria(final LendTransaction pLendTransaction) {
		DetachedCriteria criteria = DetachedCriteria.forClass(LendTransactionComment.class);
	
		if (pLendTransaction != null) {
			criteria.add(Restrictions.eq("lendTransaction", pLendTransaction));
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
}
