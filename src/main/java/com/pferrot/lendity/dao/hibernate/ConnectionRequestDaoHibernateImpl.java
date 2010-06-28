package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

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
	
	private List<ConnectionRequest> findConnectionRequestsList(final Long pConnectionId, final Long pRequesterId, 
			final Boolean pCompleted, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getConnectionRequestDetachedCriteria(pConnectionId, pRequesterId, pCompleted);
		criteria.addOrder(Order.desc("requestDate"));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public long countConnectionRequests(final Long pConnectionId, final Long pRequesterId, final Boolean pCompleted) {
		final DetachedCriteria criteria = getConnectionRequestDetachedCriteria(pConnectionId, pRequesterId, pCompleted);
		return rowCount(criteria);
	}

	private DetachedCriteria getConnectionRequestDetachedCriteria(final Long pConnectionId, final Long pRequesterId, final Boolean pCompleted) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ConnectionRequest.class);
	
		if (pCompleted != null) {
			if (pCompleted.booleanValue()) {
				criteria.add(Restrictions.isNotNull("responseDate"));
			}
			else {
				criteria.add(Restrictions.isNull("responseDate"));
			}			
		}
		
		if (pConnectionId != null) {
			final DetachedCriteria connectionCriteria = criteria.createCriteria("connection", CriteriaSpecification.INNER_JOIN);
			connectionCriteria.add(Restrictions.eq("id", pConnectionId));
		}
		
		if (pRequesterId != null) {
			final DetachedCriteria requesterCriteria = criteria.createCriteria("requester", CriteriaSpecification.INNER_JOIN);
			requesterCriteria.add(Restrictions.eq("id", pRequesterId));
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
	
	public ListWithRowCount findConnectionRequests(final Long pConnectionId, final Long pRequesterId, 
			final Boolean pCompleted, final int pFirstResult, final int pMaxResults) {
		final List list = findConnectionRequestsList(pConnectionId, pRequesterId, pCompleted, pFirstResult, pMaxResults);
		final long count = countConnectionRequests(pConnectionId, pRequesterId, pCompleted);
		
		return new ListWithRowCount(list, count);
	}	
}
