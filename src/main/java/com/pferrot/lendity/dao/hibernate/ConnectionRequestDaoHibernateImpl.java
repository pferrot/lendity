package com.pferrot.lendity.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.ConnectionRequestDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.ConnectionRequest;

public class ConnectionRequestDaoHibernateImpl extends HibernateDaoSupport implements ConnectionRequestDao {

	public Long createConnectionRequest(final ConnectionRequest pConnectionRequest) {
		return (Long)getHibernateTemplate().save(pConnectionRequest);
	}

	public void deleteConnectionRequest(final ConnectionRequest pConnectionRequest) {
		getHibernateTemplate().delete(pConnectionRequest);		
	}

	public void updateConnectionRequest(final ConnectionRequest pConnectionRequest) {
		getHibernateTemplate().update(pConnectionRequest);
	}

	public ConnectionRequest findConnectionRequest(final Long pConnectionRequestId) {
		return (ConnectionRequest)getHibernateTemplate().load(ConnectionRequest.class, pConnectionRequestId);
	}
	
	public List<ConnectionRequest> findConnectionRequestsList(final Long[] pConnectionIds, final Long[] pRequesterIds, 
			final Boolean pOrCriteria, final Long[] pExcludedConnectionIds, final Long[] pExcludedRequesterIds, final Boolean pCompleted, 
			final Long[] pResponseIds, final Date pResponseDateMin, final String pOrderByField, final Boolean pOrderByAsc,
			final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getConnectionRequestDetachedCriteria(pConnectionIds, pRequesterIds, pOrCriteria, pExcludedConnectionIds, pExcludedRequesterIds,
				pCompleted, pResponseIds, pResponseDateMin);
		if (!StringUtils.isNullOrEmpty(pOrderByField)) {
			if (Boolean.FALSE.equals(pOrderByAsc)) {
				criteria.addOrder(Order.desc(pOrderByField));
			}
			else {
				criteria.addOrder(Order.asc(pOrderByField));
			}
		}	
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public long countConnectionRequests(final Long[] pConnectionIds, final Long[] pRequesterIds, final Boolean pOrCriteria, 
			final Long[] pExcludedConnectionIds, final Long[] pExcludedRequesterIds, final Boolean pCompleted, Long[] pResponseIds, final Date pResponseDateMin) {
		final DetachedCriteria criteria = getConnectionRequestDetachedCriteria(pConnectionIds, pRequesterIds, pOrCriteria, pExcludedConnectionIds,
				pExcludedRequesterIds, pCompleted, pResponseIds, pResponseDateMin);
		return rowCount(criteria);
	}

	private DetachedCriteria getConnectionRequestDetachedCriteria(final Long[] pConnectionIds, final Long[] pRequesterIds, final Boolean pOrCriteria,
			final Long[] pExcludedConnectionIds, final Long[] pExcludedRequesterIds, final Boolean pCompleted, final Long[] pResponseIds, final Date pResponseDateMin) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConnectionRequest.class);
	
		if (pCompleted != null) {
			if (pCompleted.booleanValue()) {
				criteria.add(Restrictions.isNotNull("responseDate"));
			}
			else {
				criteria.add(Restrictions.isNull("responseDate"));
			}			
		}
		
		if (pResponseDateMin != null) {
			criteria.add(Restrictions.gt("responseDate", pResponseDateMin));
		}
		
		if (pResponseIds != null && pResponseIds.length > 0) {
			final DetachedCriteria responseCriteria = criteria.createCriteria("response", CriteriaSpecification.INNER_JOIN);
			responseCriteria.add(Restrictions.in("id", pResponseIds));
		}
		
		// Lets make an or.
		if (Boolean.TRUE.equals(pOrCriteria)) {
			if (pConnectionIds != null && pConnectionIds.length > 0 && pRequesterIds != null && pRequesterIds.length > 0) {
				final LogicalExpression expression = Restrictions.or(
					Restrictions.in("connection.id", pConnectionIds),
					Restrictions.in("requester.id", pConnectionIds)
				);			
				criteria.add(expression);
			}
		}
		if (pExcludedConnectionIds != null && pExcludedConnectionIds.length > 0) {
			criteria.add(Restrictions.not(Restrictions.in("connection.id", pExcludedConnectionIds)));
		}
		
		if (pExcludedRequesterIds != null && pExcludedRequesterIds.length > 0) {
			criteria.add(Restrictions.not(Restrictions.in("requester.id", pExcludedRequesterIds)));
		}
		
		else {
			if (pConnectionIds != null && pConnectionIds.length > 0) {
				final DetachedCriteria connectionCriteria = criteria.createCriteria("connection", CriteriaSpecification.INNER_JOIN);
				connectionCriteria.add(Restrictions.in("id", pConnectionIds));
			}	
			if (pRequesterIds != null && pRequesterIds.length > 0) {
				final DetachedCriteria requesterCriteria = criteria.createCriteria("requester", CriteriaSpecification.INNER_JOIN);
				requesterCriteria.add(Restrictions.in("id", pRequesterIds));
			}
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

	public ListWithRowCount findConnectionRequests(final Long[] pConnectionIds, final Long[] pRequesterIds, final Boolean pOrCriteria, final Long[] pExcludedConnectionIds, final Long[] pExcludedRequesterIds, 
			final Boolean pCompleted, final Long[] pResponseIds, final Date pResponseDateMin, final String pOrderByField, final Boolean pOrderByAsc,
			final int pFirstResult, final int pMaxResults) {
		final List list = findConnectionRequestsList(pConnectionIds, pRequesterIds, pOrCriteria, pExcludedConnectionIds, pExcludedRequesterIds, pCompleted, pResponseIds, pResponseDateMin, pOrderByField, pOrderByAsc, pFirstResult, pMaxResults);
		final long count = countConnectionRequests(pConnectionIds, pRequesterIds, pOrCriteria, pExcludedConnectionIds, pExcludedRequesterIds, pCompleted, pResponseIds, pResponseDateMin);
		
		return new ListWithRowCount(list, count);
	}
}
