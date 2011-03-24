package com.pferrot.lendity.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pferrot.lendity.dao.LendRequestDao;
import com.pferrot.lendity.dao.bean.ListWithRowCount;
import com.pferrot.lendity.model.LendRequest;

public class LendRequestDaoHibernateImpl extends HibernateDaoSupport implements LendRequestDao {

	public Long createLendRequest(final LendRequest pLendRequest) {
		return (Long)getHibernateTemplate().save(pLendRequest);
	}

	public void deleteLendRequest(final LendRequest pLendRequest) {
		getHibernateTemplate().delete(pLendRequest);		
	}
	
	public void deleteLendRequestsForItem(final Long pItemId) {		
		getHibernateTemplate().bulkUpdate("delete from LendRequest where item.id = ?", pItemId); 		
	}
	
	public void updateLendRequestsSetNullItem(final Long pItemId) {		
		getHibernateTemplate().bulkUpdate("update LendRequest set item = null where item.id = ?", pItemId); 		
	}

	public void updateLendRequest(final LendRequest pLendRequest) {
		getHibernateTemplate().update(pLendRequest);
	}

	public LendRequest findLendRequest(final Long pLendRequestId) {
		return (LendRequest)getHibernateTemplate().load(LendRequest.class, pLendRequestId);
	}
	
	private List<LendRequest> findLendRequestsList(final Long pRequesterId, final Long pOwnerId, final Long pItemId, 
			final Boolean pCompleted, final int pFirstResult, final int pMaxResults) {
		final DetachedCriteria criteria = getLendRequestDetachedCriteria(pRequesterId, pOwnerId, pItemId, pCompleted);
		criteria.addOrder(Order.desc("requestDate"));
		
		return getHibernateTemplate().findByCriteria(criteria, pFirstResult, pMaxResults);		
	}

	public long countLendRequests(final Long pRequesterId, final Long pOwnerId, final Long pItemId, final Boolean pCompleted) {
		final DetachedCriteria criteria = getLendRequestDetachedCriteria(pRequesterId, pOwnerId, pItemId, pCompleted);
		return rowCount(criteria);
	}

	private DetachedCriteria getLendRequestDetachedCriteria(final Long pRequesterId, final Long pOwnerId, final Long pItemId, final Boolean pCompleted) {
		DetachedCriteria criteria = DetachedCriteria.forClass(LendRequest.class);
	
		if (pCompleted != null) {
			if (pCompleted.booleanValue()) {
				criteria.add(Restrictions.isNotNull("response"));
			}
			else {
				criteria.add(Restrictions.isNull("response"));
			}			
		}
		
		if (pItemId != null) {
			final DetachedCriteria itemCriteria = criteria.createCriteria("item", CriteriaSpecification.INNER_JOIN);
			itemCriteria.add(Restrictions.eq("id", pItemId));
		}
		
		if (pRequesterId != null) {
			final DetachedCriteria requesterCriteria = criteria.createCriteria("requester", CriteriaSpecification.INNER_JOIN);
			requesterCriteria.add(Restrictions.eq("id", pRequesterId));
		}

		if (pOwnerId != null) {
			final DetachedCriteria ownerCriteria = criteria.createCriteria("owner", CriteriaSpecification.INNER_JOIN);
			ownerCriteria.add(Restrictions.eq("id", pOwnerId));
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
	
	public ListWithRowCount findLendRequests(final Long pRequesterId, final Long pOwnerId, final Long pItemId, 
			final Boolean pCompleted, final int pFirstResult, final int pMaxResults) {
		final List list = findLendRequestsList(pRequesterId, pOwnerId, pItemId, pCompleted, pFirstResult, pMaxResults);
		final long count = countLendRequests(pRequesterId, pOwnerId, pItemId, pCompleted);
		
		return new ListWithRowCount(list, count);
	}	
}
