package com.pferrot.lendity.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.LendTransactionDao;
import com.pferrot.lendity.dao.ListValueDao;
import com.pferrot.lendity.dao.bean.LendTransactionDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.LendTransaction;
import com.pferrot.lendity.model.LendTransactionStatus;

public class LendTransactionDaoHibernateImpl extends HibernateDaoSupport implements LendTransactionDao {
	
	private ListValueDao listValueDao;

	public ListValueDao getListValueDao() {
		return listValueDao;
	}

	public void setListValueDao(ListValueDao listValueDao) {
		this.listValueDao = listValueDao;
	}

	public Long createLendTransaction(final LendTransaction pLendTransaction) {
		return (Long)getHibernateTemplate().save(pLendTransaction);
	}
	
	public void refreshLendTransaction(final LendTransaction pLendTransaction) {
		getHibernateTemplate().refresh(pLendTransaction);
	}

	public void deleteLendTransaction(final LendTransaction pLendTransaction) {
		getHibernateTemplate().delete(pLendTransaction);		
	}
	
	public void deleteLendTransactionsForItem(final Long pItemId) {		
		getHibernateTemplate().bulkUpdate("delete from LendTransaction where item.id = ?", pItemId); 		
	}
	
	public void updateLendTransactionsSetNullItem(final Long pItemId) {		
		getHibernateTemplate().bulkUpdate("update LendTransaction set item = null where item.id = ?", pItemId); 		
	}
	

	public void updateLendTransaction(final LendTransaction pLendTransaction) {
		getHibernateTemplate().update(pLendTransaction);
	}

	public LendTransaction findLendTransaction(final Long pLendTransactionId) {
		return (LendTransaction)getHibernateTemplate().load(LendTransaction.class, pLendTransactionId);
	}
	
	public List<LendTransaction> findLendTransactionsList(final LendTransactionDaoQueryBean pLendTransactionDaoQueryBean) {
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
		
		if (pLendTransactionDaoQueryBean.getItemId() != null) {
			final DetachedCriteria requesterCriteria = criteria.createCriteria("item", CriteriaSpecification.INNER_JOIN);
			requesterCriteria.add(Restrictions.eq("id", pLendTransactionDaoQueryBean.getItemId()));
		}
		
		if (pLendTransactionDaoQueryBean.getStartDateMin() != null) {
			criteria.add(Restrictions.ge("startDate", pLendTransactionDaoQueryBean.getStartDateMin()));
		}
		
		if (pLendTransactionDaoQueryBean.getStartDateMax() != null) {
			criteria.add(Restrictions.le("startDate", pLendTransactionDaoQueryBean.getStartDateMax()));
		}
		
		if (pLendTransactionDaoQueryBean.getEndDateMin() != null) {
			criteria.add(Restrictions.ge("endDate", pLendTransactionDaoQueryBean.getEndDateMin()));
		}
		
		if (pLendTransactionDaoQueryBean.getEndDateMax() != null) {
			criteria.add(Restrictions.le("endDate", pLendTransactionDaoQueryBean.getEndDateMax()));
		}
		
		if (Boolean.TRUE.equals(pLendTransactionDaoQueryBean.getToEvaluateByBorrower())) {
			criteria.add(Restrictions.and(
							Restrictions.isNotNull("borrower"), 
							Restrictions.isNull("evaluationByBorrower")));			
		}
		else if (Boolean.FALSE.equals(pLendTransactionDaoQueryBean.getToEvaluateByBorrower())) {
			throw new RuntimeException("Not implemented");			
		}
		
		if (Boolean.TRUE.equals(pLendTransactionDaoQueryBean.getToEvaluateByLender())) {
			criteria.add(Restrictions.and(
							Restrictions.isNotNull("borrower"), 
					        Restrictions.isNull("evaluationByLender")));			
		}
		else if (Boolean.FALSE.equals(pLendTransactionDaoQueryBean.getToEvaluateByLender())) {
			throw new RuntimeException("Not implemented");			
		}
		
		return criteria;	
	}

	public List<LendTransaction> findLendTransactionsWaitingForInputList(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getLendTransactionsWaitingForInputDetachedCriteria(pPersonId, pPersonId);
		
		
		return getHibernateTemplate().findByCriteria(criteria,
													 pFirstResult,
													 pMaxResults);		
	}
	
	public List<LendTransaction> findLendTransactionsAsBorrowerWaitingForInputList(final Long pBorrowerId, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getLendTransactionsWaitingForInputDetachedCriteria(null, pBorrowerId);
		
		
		return getHibernateTemplate().findByCriteria(criteria,
													 pFirstResult,
													 pMaxResults);		
	}
	
	public List<LendTransaction> findLendTransactionsAsLenderWaitingForInputList(final Long pLenderId, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getLendTransactionsWaitingForInputDetachedCriteria(pLenderId, null);
		
		
		return getHibernateTemplate().findByCriteria(criteria,
													 pFirstResult,
													 pMaxResults);		
	}
	
	public long countLendTransactionsWaitingForInput(final Long pPersonId) {
		final DetachedCriteria criteria = getLendTransactionsWaitingForInputDetachedCriteria(pPersonId, pPersonId);
		return rowCount(criteria);
	}
	
	public long countLendTransactionsAsBorrowerWaitingForInput(final Long pBorrowerId) {
		final DetachedCriteria criteria = getLendTransactionsWaitingForInputDetachedCriteria(null, pBorrowerId);
		return rowCount(criteria);
	}
	
	public long countLendTransactionsAsLenderWaitingForInput(final Long pLenderId) {
		final DetachedCriteria criteria = getLendTransactionsWaitingForInputDetachedCriteria(pLenderId, null);
		return rowCount(criteria);
	}

	private DetachedCriteria getLendTransactionsWaitingForInputDetachedCriteria(final Long pLenderId, final Long pBorrowerId) {
		if (pLenderId == null && pBorrowerId == null) {
			throw new AssertionError("At least of of lender ID or borrower ID must be defined");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LendTransaction.class);
		
		final Date now = new Date();
		
		
		
		
		Criterion lenderOrBorrowerExpression = null;
		if (pLenderId != null && pBorrowerId != null) {
			lenderOrBorrowerExpression = Restrictions.or(
														Restrictions.eq("lender.id", pLenderId),
														Restrictions.eq("borrower.id", pBorrowerId));
		}
		else if (pLenderId != null) {
			lenderOrBorrowerExpression = Restrictions.eq("lender.id", pLenderId);
		}
		else {
			lenderOrBorrowerExpression = Restrictions.eq("borrower.id", pBorrowerId);
		}
		
		final SimpleExpression startDatePassedExpression = Restrictions.le("startDate", now);
		final SimpleExpression endDatePassedExpression = Restrictions.le("endDate", now);
		
		final LendTransactionStatus initializedStatus = (LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.INITIALIZED_LABEL_CODE);
		final LendTransactionStatus openedStatus = (LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.OPENED_LABEL_CODE);
		final LendTransactionStatus inProgressStatus = (LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.IN_PROGRESS_LABEL_CODE);
		final LendTransactionStatus completedStatus = (LendTransactionStatus)listValueDao.findListValue(LendTransactionStatus.COMPLETED_LABEL_CODE);
		
		final SimpleExpression initializedCriteria = Restrictions. eq("status.id", initializedStatus.getId());
		final SimpleExpression openedCriteria = Restrictions.eq("status.id", openedStatus.getId());
		final SimpleExpression inProgressCriteria = Restrictions.eq("status.id", inProgressStatus.getId());
		
		
		LogicalExpression le =  null;
		final LogicalExpression openedAndOverdue = Restrictions.and(openedCriteria, startDatePassedExpression);
		final LogicalExpression inProgressAndOverdue = Restrictions.and(inProgressCriteria, endDatePassedExpression);
		
		le = Restrictions.or(openedAndOverdue, inProgressAndOverdue);
		
		le = Restrictions.and(lenderOrBorrowerExpression, le);
		
		SimpleExpression lenderCriteria = null;
		if (pLenderId != null) {			
			lenderCriteria = Restrictions.eq("lender.id", pLenderId);
			final LogicalExpression lenderAndInitialized = Restrictions.and(lenderCriteria, initializedCriteria);
			le = Restrictions.or(le, lenderAndInitialized);
		}
		
		// This will take the lend transactions that must be evaluated.
		final SimpleExpression completedCriteria = Restrictions.eq("status.id", completedStatus.getId());
		
		LogicalExpression lenderAndCompletedAndNotEvaluatedAndBorrowerNotNull = null;
		if (pLenderId != null) {
			final Criterion lenderEvaluationNullCriteria = Restrictions.isNull("evaluationByLender");
			final Criterion borrowerNotNullCriteria = Restrictions.isNotNull("borrower");
			lenderAndCompletedAndNotEvaluatedAndBorrowerNotNull = Restrictions.and(lenderCriteria, Restrictions.and(completedCriteria, Restrictions.and(lenderEvaluationNullCriteria, borrowerNotNullCriteria)));
		}
		
		LogicalExpression borrowerAndCompletedAndNotEvaluated = null;
		if (pBorrowerId != null) {
			final SimpleExpression borrowerCriteria = Restrictions.eq("borrower.id", pBorrowerId);
			final Criterion borrowerEvaluationNullCriteria = Restrictions.isNull("evaluationByBorrower");
			borrowerAndCompletedAndNotEvaluated = Restrictions.and(borrowerCriteria, Restrictions.and(completedCriteria, borrowerEvaluationNullCriteria));
		}
		
		if (pLenderId != null && pBorrowerId != null) {
			le = Restrictions.or(le, Restrictions.or(lenderAndCompletedAndNotEvaluatedAndBorrowerNotNull, borrowerAndCompletedAndNotEvaluated));
		}
		else if (pLenderId != null) {
			le = Restrictions.or(le, lenderAndCompletedAndNotEvaluatedAndBorrowerNotNull);
		}
		else {
			le = Restrictions.or(le, borrowerAndCompletedAndNotEvaluated);
		}
		
		criteria.add(le);
		
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

	public ListWithRowCount findLendTransactionsWaitingForInput(final Long pPersonId, final int pFirstResult, final int pMaxResults) {
		final List list = findLendTransactionsWaitingForInputList(pPersonId, pFirstResult, pMaxResults);
		final long count = countLendTransactionsWaitingForInput(pPersonId);
		
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findLendTransactionsAsLenderWaitingForInput(final Long pLenderId, final int pFirstResult, final int pMaxResults) {
		final List list = findLendTransactionsAsLenderWaitingForInputList(pLenderId, pFirstResult, pMaxResults);
		final long count = countLendTransactionsAsLenderWaitingForInput(pLenderId);
		
		return new ListWithRowCount(list, count);
	}
	
	public ListWithRowCount findLendTransactionsAsBorrowerWaitingForInput(final Long pBorrowerId, final int pFirstResult, final int pMaxResults) {
		final List list = findLendTransactionsAsBorrowerWaitingForInputList(pBorrowerId, pFirstResult, pMaxResults);
		final long count = countLendTransactionsAsBorrowerWaitingForInput(pBorrowerId);
		
		return new ListWithRowCount(list, count);
	}
}
