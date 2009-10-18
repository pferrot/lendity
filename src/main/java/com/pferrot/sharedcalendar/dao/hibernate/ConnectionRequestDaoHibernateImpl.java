package com.pferrot.sharedcalendar.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
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
		
		final Criterion personCriterion = Restrictions.eq(byRequester?"requester":"connection", pPerson);
		Criterion finalCriterion = null;
		if (uncompletedOnly) {
			final Criterion uncompletedCriteria = Restrictions.eq("responseDate", null);
			finalCriterion = Restrictions.and(personCriterion, uncompletedCriteria);
		}
		else {
			finalCriterion = personCriterion;
		}
		DetachedCriteria critera = DetachedCriteria.forClass(ConnectionRequest.class).add(finalCriterion);		
		return getHibernateTemplate().findByCriteria(critera, pFirstResult, pMaxResults);
	}
}
