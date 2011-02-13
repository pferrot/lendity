package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.bean.LendTransactionDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.LendTransaction;

public class LendTransactionDaoHibernateImpl extends HibernateDaoSupport implements LendTransactionDao {

	public Long createLendTransaction(final LendTransaction pLendTransaction) {
		return (Long)getHibernateTemplate().save(pLendTransaction);
	}

	public void deleteLendTransaction(final LendTransaction pLendTransaction) {
		getHibernateTemplate().delete(pLendTransaction);		
	}
	
	public void deleteLendTransactionsForItem(final Long pItemId) {		
		getHibernateTemplate().bulkUpdate("delete from LendTransaction where item.id = ?", pItemId); 		
	}

	public void updateLendTransaction(final LendTransaction pLendTransaction) {
		getHibernateTemplate().update(pLendTransaction);
	}

	public LendTransaction findLendTransaction(final Long pLendTransactionId) {
		return (LendTransaction)getHibernateTemplate().load(LendTransaction.class, pLendTransactionId);
	}
	
	private List<LendTransaction> findLendTransactionsList(final LendTransactionDaoQueryBean pLendTransactionDaoQueryBean) {
		final DetachedCriteria criteria = getLendTransactionDetachedCriteria(pLendTransactionDaoQueryBean);
		
		if (!StringUtils.isNullOrEmpty(pLendTransactionDaoQueryBean.getOrderBy())) {
			if (Boolean.TRUE.equals(pLendTransactionDaoQueryBean.getOrderByAscending())) {
				criteria.addOrder(Order.asc(pLendTransactionDaoQueryBean.getOrderBy()));
			}
			else {
				criteria.addOrder(Order.desc(pLendTransactionDaoQueryBean.getOrderBy()));
			}
		}
		
		return getHibernateTemplate().findByCriteria(criteria,
				pLendTransactionDaoQueryBean.getFirstResult(),
				pLendTransactionDaoQueryBean.getMaxResults());		
	}

	public long countLendTransactions(final LendTransactionDaoQueryBean pLendTransactionDaoQueryBean) {
		final DetachedCriteria criteria = getLendTransactionDetachedCriteria(pLendTransactionDaoQueryBean);
		return rowCount(criteria);
	}

	private DetachedCriteria getLendTransactionDetachedCriteria(final LendTransactionDaoQueryBean pLendTransactionDaoQueryBean) {
		DetachedCriteria criteria = DetachedCriteria.forClass(LendTransaction.class);
		
		if (pLendTransactionDaoQueryBean.getStatusIds() != null) {
			final DetachedCriteria itemCriteria = criteria.createCriteria("status", CriteriaSpecification.INNER_JOIN);
			itemCriteria.add(Restrictions.in("id", pLendTransactionDaoQueryBean.getStatusIds()));
		}
		
		if (pLendTransactionDaoQueryBean.getLendRequestId() != null) {
			final DetachedCriteria itemCriteria = criteria.createCriteria("lendRequest", CriteriaSpecification.INNER_JOIN);
			itemCriteria.add(Restrictions.eq("id", pLendTransactionDaoQueryBean.getLendRequestId()));
		}
		
		if (pLendTransactionDaoQueryBean.getBorrowerOrLenderId() != null) {
			Criterion c = Restrictions.or(
					Restrictions.eq("borrower.id", pLendTransactionDaoQueryBean.getBorrowerOrLenderId()),
					Restrictions.eq("lender.id", pLendTransactionDaoQueryBean.getBorrowerOrLenderId()));
			final DetachedCriteria requesterCriteria = criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN);
			requesterCriteria.add(Restrictions.eq("id", pLendTransactionDaoQueryBean.getBorrowerId()));
			criteria.add(c);
		}
		
		if (pLendTransactionDaoQueryBean.getBorrowerId() != null) {
			final DetachedCriteria requesterCriteria = criteria.createCriteria("borrower", CriteriaSpecification.INNER_JOIN);
			requesterCriteria.add(Restrictions.eq("id", pLendTransactionDaoQueryBean.getBorrowerId()));
		}
		
		if (pLendTransactionDaoQueryBean.getLenderId() != null) {
			final DetachedCriteria requesterCriteria = criteria.createCriteria("lender", CriteriaSpecification.INNER_JOIN);
			requesterCriteria.add(Restrictions.eq("id", pLendTransactionDaoQueryBean.getLenderId()));
		}
		
		if (pLendTransactionDaoQueryBean.getInternalItemId() != null) {
			final DetachedCriteria requesterCriteria = criteria.createCriteria("item", CriteriaSpecification.INNER_JOIN);
			requesterCriteria.add(Restrictions.eq("id", pLendTransactionDaoQueryBean.getInternalItemId()));
		}
		
		return criteria;	
	}

	/**
	 * Returns the number of rows for a given DetachedCriteria.
	 *
	 * @param pCriteria
	 * @return
	 */
	private long rowCount(final DetachedCriteria pCriteria) {
		pCriteria.setProjection(Projections.rowCount());
		return ((Long)getHibernateTemplate().findByCriteria(pCriteria).get(0)).longValue();
	}
	
	public ListWithRowCount findLendTransactions(final LendTransactionDaoQueryBean pLendTransactionDaoQueryBean) {
		final List list = findLendTransactionsList(pLendTransactionDaoQueryBean);
		final long count = countLendTransactions(pLendTransactionDaoQueryBean);
		
		return new ListWithRowCount(list, count);
	}	
}
