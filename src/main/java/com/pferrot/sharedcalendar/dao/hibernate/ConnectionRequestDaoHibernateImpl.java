package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.sharedcalendar.dao.ConnectionRequestDao;
import com.pferrot.sharedcalendar.model.ConnectionRequest;
import com.pferrot.sharedcalendar.model.Person;

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

	public List<ConnectionRequest> findConnectionRequestByConnection(
			final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findConnectionRequest(pPerson, pFirstResult, pMaxResults, false, false);
	}

	public List<ConnectionRequest> findUncompletedConnectionRequestByConnection(
			final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findConnectionRequest(pPerson, pFirstResult, pMaxResults, false, true);
	}

	public List<ConnectionRequest> findConnectionRequestByRequester(
			final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findConnectionRequest(pPerson, pFirstResult, pMaxResults, true, false);
	}

	public List<ConnectionRequest> findUncompletedConnectionRequestByRequester(
			final Person pPerson, final int pFirstResult, final int pMaxResults) {
		return findConnectionRequest(pPerson, pFirstResult, pMaxResults, true, true);
	}	
	
	private List<ConnectionRequest> findConnectionRequest(
			final Person pPerson, 
			final int pFirstResult, final int pMaxResults, 
			final boolean byRequester, final boolean uncompletedOnly) {
		
		CoreUtils.assertNotNull(pPerson);
		
		DetachedCriteria critera = DetachedCriteria.forClass(ConnectionRequest.class).
			addOrder(Order.desc("requestDate"));
		
		if (uncompletedOnly) {
			critera.add(Restrictions.isNull("responseDate"));
		}
		
		critera.createCriteria(byRequester?"requester":"connection", CriteriaSpecification.INNER_JOIN).
			add(Restrictions.eq("id", pPerson.getId()));
		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}

	private List<ConnectionRequest> findConnectionRequestByRequesterAndConnection(
			final Person pPerson1, final Person pPerson2, final int pFirstResult,
			final int pMaxResults, final boolean uncompletedOnly) {
	
		CoreUtils.assertNotNull(pPerson1);
		CoreUtils.assertNotNull(pPerson2);
		
		Criterion requesterCriterion = Restrictions.eq("requester", pPerson1);
		Criterion connectionCriterion = Restrictions.eq("connection", pPerson2);

		final Criterion personCriterion1 = Restrictions.and(requesterCriterion, connectionCriterion);

		requesterCriterion = Restrictions.eq("requester", pPerson2);
		connectionCriterion = Restrictions.eq("connection", pPerson1);

		final Criterion personCriterion2 = Restrictions.and(requesterCriterion, connectionCriterion);
		
		final Criterion personCriterion = Restrictions.or(personCriterion1, personCriterion2);
		
		Criterion finalCriterion = null;
		if (uncompletedOnly) {
			final Criterion uncompletedCriteria = Restrictions.isNull("responseDate");
			finalCriterion = Restrictions.and(personCriterion, uncompletedCriteria);
		}
		else {
			finalCriterion = personCriterion;
		}
		DetachedCriteria critera = DetachedCriteria.forClass(ConnectionRequest.class).add(finalCriterion);		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}

	public List<ConnectionRequest> findConnectionRequestByRequesterAndConnection(
			Person pPerson1, Person pPerson2, int pFirstResult,
			int pMaxResults) {
		return findConnectionRequestByRequesterAndConnection(pPerson1, pPerson2, pFirstResult, pMaxResults, false);
	}

	public List<ConnectionRequest> findUncompletedConnectionRequestByRequesterAndConnection(
			Person pPerson1, Person pPerson2, int pFirstResult,
			int pMaxResults) {
		return findConnectionRequestByRequesterAndConnection(pPerson1, pPerson2, pFirstResult, pMaxResults, true);
	}
}
