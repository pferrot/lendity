package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.lendity.dao.EvaluationDao;
import com.pferrot.lendity.dao.bean.EvaluationDaoQueryBean;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.Evaluation;

public class EvaluationDaoHibernateImpl extends HibernateDaoSupport implements EvaluationDao {

	public Long createEvaluation(final Evaluation pEvaluation) {
		return (Long)getHibernateTemplate().save(pEvaluation);
	}

	public void deleteEvaluation(final Evaluation pEvaluation) {
		getHibernateTemplate().delete(pEvaluation);		
	}

	public void updateEvaluation(final Evaluation pEvaluation) {
		getHibernateTemplate().update(pEvaluation);
	}

	public Evaluation findEvaluation(final Long pEvaluationId) {
		return (Evaluation)getHibernateTemplate().load(Evaluation.class, pEvaluationId);
	}
	
	public List<Evaluation> findEvaluationsList(final EvaluationDaoQueryBean pEvaluationDaoQueryBean) {
		final DetachedCriteria criteria = getEvaluationsDetachedCriteria(pEvaluationDaoQueryBean);
		
		if (Boolean.TRUE.equals(pEvaluationDaoQueryBean.getOrderByAscending())) {
			criteria.addOrder(Order.asc(pEvaluationDaoQueryBean.getOrderBy()));		
		}
		else {
			criteria.addOrder(Order.desc(pEvaluationDaoQueryBean.getOrderBy()));
		}
		
		return getHibernateTemplate().findByCriteria(criteria, pEvaluationDaoQueryBean.getFirstResult(), pEvaluationDaoQueryBean.getMaxResults());		
	}

	public long countEvaluations(final EvaluationDaoQueryBean pEvaluationDaoQueryBean) {
		final DetachedCriteria criteria = getEvaluationsDetachedCriteria(pEvaluationDaoQueryBean);
		return rowCount(criteria);
	}

	private DetachedCriteria getEvaluationsDetachedCriteria(final EvaluationDaoQueryBean pEvaluationDaoQueryBean) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Evaluation.class);
		
		if (pEvaluationDaoQueryBean.getEvaluatedId() != null) {
			criteria.add(Restrictions.eq("evaluated.id", pEvaluationDaoQueryBean.getEvaluatedId()));
		}
		
		if (pEvaluationDaoQueryBean.getEvaluatorId() != null) {
			criteria.add(Restrictions.eq("evaluator.id", pEvaluationDaoQueryBean.getEvaluatorId()));
		}

		if (pEvaluationDaoQueryBean.getLendTransactionId() != null) {
			criteria.add(Restrictions.eq("lendTransaction.id", pEvaluationDaoQueryBean.getLendTransactionId()));
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

	public ListWithRowCount findEvaluations(final EvaluationDaoQueryBean pEvaluationDaoQueryBean) {
		final List list = findEvaluationsList(pEvaluationDaoQueryBean);
		final long count = countEvaluations(pEvaluationDaoQueryBean);
		return new ListWithRowCount(list, count);
	}
}
