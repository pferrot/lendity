package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.core.CoreUtils;
import com.pferrot.core.StringUtils;
import com.pferrot.lendity.dao.PotentialConnectionDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.dao.bean.PotentialConnectionDaoQueryBean;
import com.pferrot.lendity.model.PotentialConnection;


public class PotentialConnectionDaoHibernateImpl extends HibernateDaoSupport implements PotentialConnectionDao {

	public Long createPotentialConnection(final PotentialConnection pPotentialConnection) {
		return (Long)getHibernateTemplate().save(pPotentialConnection);
	}

	public void deletePotentialConnection(final PotentialConnection pPotentialConnection) {
		getHibernateTemplate().delete(pPotentialConnection);				
	}
	
	public void deletePotentialConnectionsForPerson(final Long pPersonId) {
		getHibernateTemplate().bulkUpdate("delete from PotentialConnection where person.id = ?", pPersonId); 		
	}
	
	public void deletePotentialConnectionForPersonAndConnection(final Long pPersonId, final Long pConnectionId) {
		// Prevent deleting all potential connections for 1 person by mistake...
		CoreUtils.assertNotNull(pConnectionId);
		getHibernateTemplate().bulkUpdate("delete from PotentialConnection where person.id = ? and connection.id = ?", new Object[]{pPersonId, pConnectionId}); 		
	}

	public PotentialConnection findPotentialConnection(final Long pPotentialConnectionId) {
		return (PotentialConnection)getHibernateTemplate().load(PotentialConnection.class, pPotentialConnectionId);
	}

	public ListWithRowCount findPotentialConnections(final PotentialConnectionDaoQueryBean pQueryBean) {
		final List list = findPotentialConnectionsList(pQueryBean);
		final long count = countPotentialConnections(pQueryBean);

		return new ListWithRowCount(list, count);
	}

	public long countPotentialConnections(final PotentialConnectionDaoQueryBean pQueryBean) {
		final DetachedCriteria criteria = getPotentialConnectionDetachedCriteria(pQueryBean);
		return rowCount(criteria);
	}

	public List<PotentialConnection> findPotentialConnectionsList(final PotentialConnectionDaoQueryBean pQueryBean) {
		DetachedCriteria criteria = getPotentialConnectionDetachedCriteria(pQueryBean);
				
		if (!StringUtils.isNullOrEmpty(pQueryBean.getOrderBy())) {
			// Ascending.
			if (pQueryBean.getOrderByAscending() == null || pQueryBean.getOrderByAscending().booleanValue()) {
				criteria.addOrder(Order.asc(pQueryBean.getOrderBy()).ignoreCase());
			}
			// Descending.
			else {
				criteria.addOrder(Order.desc(pQueryBean.getOrderBy()).ignoreCase());
			}
		}
		return getHibernateTemplate().findByCriteria(criteria, pQueryBean.getFirstResult(), pQueryBean.getMaxResults());
	}

	public void updatePotentialConnection(final PotentialConnection pPotentialConnection) {
		getHibernateTemplate().update(pPotentialConnection);	
		
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

	private DetachedCriteria getPotentialConnectionDetachedCriteria(final PotentialConnectionDaoQueryBean pQueryBean) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PotentialConnection.class);
		
		if (!StringUtils.isNullOrEmpty(pQueryBean.getEmail())) {
			criteria.add(Restrictions.ilike("email", pQueryBean.getEmail(), MatchMode.EXACT));
		}
		
		if (!StringUtils.isNullOrEmpty(pQueryBean.getName())) {
			criteria.add(Restrictions.ilike("name", pQueryBean.getName(), MatchMode.ANYWHERE));
		}
		
		if (!StringUtils.isNullOrEmpty(pQueryBean.getSource())) {
			criteria.add(Restrictions.ilike("source", pQueryBean.getSource(), MatchMode.EXACT));
		}
		
		if (pQueryBean.getInvitationSent() != null) {
			if (pQueryBean.getInvitationSent().booleanValue()) {
				criteria.add(Restrictions.isNotNull("invitationSentOn"));
			}
			else {
				criteria.add(Restrictions.isNull("invitationSentOn"));
			}
		}
		
		if (pQueryBean.getIgnored() != null) {
			criteria.add(Restrictions.eq("ignored", pQueryBean.getIgnored()));
		}
		
		if (pQueryBean.getPersonId() != null) {
			criteria.add(Restrictions.eq("person.id", pQueryBean.getPersonId()));
		}
		
		DetachedCriteria personCriteria = null;
		if (pQueryBean.getPersonEnabled() != null) {
			personCriteria = criteria.createAlias("person", "p", CriteriaSpecification.INNER_JOIN);
			personCriteria.add(Restrictions.eq("p.enabled", pQueryBean.getPersonEnabled()));
		}
		
		if (pQueryBean.getConnectionExists() != null) {
			if (pQueryBean.getConnectionExists().booleanValue()) {
				criteria.add(Restrictions.isNotNull("connection"));
			}
			else {
				criteria.add(Restrictions.isNull("connection"));
			}
		}
		
		if (pQueryBean.getAlreadyConnected() != null) {
			if (pQueryBean.getPersonConnectionsIds() == null) {
				throw new RuntimeException("personConnectionsIds must be defined");
			}
			if (pQueryBean.getAlreadyConnected().booleanValue()) {
				criteria.add(Restrictions.in("connection.id", pQueryBean.getPersonConnectionsIds()));
			}
			else {
				criteria.add(Restrictions.not(Restrictions.in("connection.id", pQueryBean.getPersonConnectionsIds())));
			}
		}
		
		if (pQueryBean.getBannedByPerson() != null) {
			if (pQueryBean.getBannedByPersonsIds() == null) {
				throw new RuntimeException("bannedByPersonsIds must be defined");
			}
			if (pQueryBean.getBannedByPerson().booleanValue()) {
				criteria.add(Restrictions.in("connection.id", pQueryBean.getBannedByPersonsIds()));
			}
			else {
				criteria.add(Restrictions.not(Restrictions.in("connection.id", pQueryBean.getBannedByPersonsIds())));
			}
		}
		
		if (pQueryBean.getConnectionRequestPending() != null) {
			if (pQueryBean.getPendingConnectionRequestConnectionsIds() == null) {
				throw new RuntimeException("pendingConnectionRequestConnectionsIds must be defined");
			}
			if (pQueryBean.getConnectionRequestPending().booleanValue()) {
				criteria.add(Restrictions.in("connection.id", pQueryBean.getPendingConnectionRequestConnectionsIds()));
			}
			else {
				criteria.add(Restrictions.not(Restrictions.in("connection.id", pQueryBean.getPendingConnectionRequestConnectionsIds())));
			}
		}
		
		DetachedCriteria connectionCriteria = null;
		if (pQueryBean.getConnectionEnabled() != null) {
			connectionCriteria = criteria.createAlias("connection", "c", CriteriaSpecification.INNER_JOIN);
			connectionCriteria.add(Restrictions.eq("c.enabled", pQueryBean.getConnectionEnabled()));
		}
				
		return criteria;
	}

}